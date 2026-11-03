package com.clickretina.brewkery.data.repository

import com.clickretina.brewkery.data.mapper.toDomain
import com.clickretina.brewkery.data.remote.BrewkeryApiService
import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.Resource
import com.clickretina.brewkery.domain.model.StoreMeta
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class BrewkeryRepositoryImpl(
    private val apiService: BrewkeryApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BrewkeryRepository {

    private val _menuState = MutableStateFlow<Resource<List<ProductItem>>>(Resource.Loading)
    override val menuState: StateFlow<Resource<List<ProductItem>>> = _menuState.asStateFlow()

    private val _storeMeta = MutableStateFlow(StoreMeta())
    override val storeMeta: StateFlow<StoreMeta> = _storeMeta.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    override val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _activeOrder = MutableStateFlow<ActiveOrder?>(null)
    override val activeOrder: StateFlow<ActiveOrder?> = _activeOrder.asStateFlow()

    // Single source of truth cache for fast lookup
    private val itemsCache = ConcurrentHashMap<Int, ProductItem>()

    override suspend fun getMenu(forceRefresh: Boolean): Resource<List<ProductItem>> =
        withContext(ioDispatcher) {
            // Return cached items if already loaded and refresh not forced
            if (!forceRefresh && _menuState.value is Resource.Success && itemsCache.isNotEmpty()) {
                val cachedList = itemsCache.values.sortedBy { it.id }
                return@withContext Resource.Success(cachedList)
            }

            _menuState.value = Resource.Loading

            try {
                val response = apiService.getMenu()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        body.meta?.let { _storeMeta.value = it.toDomain() }
                        val categoriesList = body.categories.map { it.toDomain() }
                        _categories.value = categoriesList

                        val productList = body.items.map { it.toDomain() }
                        itemsCache.clear()
                        productList.forEach { itemsCache[it.id] = it }

                        val successResult = Resource.Success(productList)
                        _menuState.value = successResult
                        successResult
                    } else {
                        val error = Resource.Error("API returned empty body")
                        _menuState.value = error
                        error
                    }
                } else {
                    val error = Resource.Error("HTTP ${response.code()}: ${response.message()}")
                    _menuState.value = error
                    error
                }
            } catch (e: IOException) {
                // Network connection or timeout error
                val cachedList = itemsCache.values.sortedBy { it.id }
                val error = if (cachedList.isNotEmpty()) {
                    Resource.Success(cachedList)
                } else {
                    Resource.Error("Network error: Please check your internet connection.", e)
                }
                _menuState.value = error
                error
            } catch (e: Exception) {
                // Serialization or unexpected error
                val error = Resource.Error(e.localizedMessage ?: "Unexpected error occurred", e)
                _menuState.value = error
                error
            }
        }

    override suspend fun getItemDetail(id: Int): Resource<ProductItem> =
        withContext(ioDispatcher) {
            try {
                val response = apiService.getItemById(id)
                if (response.isSuccessful && response.body() != null) {
                    val item = response.body()!!.toDomain()
                    itemsCache[item.id] = item
                    Resource.Success(item)
                } else {
                    itemsCache[id]?.let { Resource.Success(it) }
                        ?: Resource.Error("Failed to fetch product #$id: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                // Fall back to in-memory menu cache if network drops
                itemsCache[id]?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Failed to load product details: ${e.localizedMessage}", e)
            }
        }

    override fun addToCart(
        product: ProductItem,
        size: ProductSizeOption,
        milk: ProductMilkOption,
        sugar: String,
        quantity: Int
    ) {
        if (quantity <= 0) return

        _cartItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst {
                it.product?.id == product.id &&
                    it.selectedSize.id == size.id &&
                    it.selectedMilk.id == milk.id &&
                    it.selectedSugar == sugar
            }

            if (existingIndex >= 0) {
                currentList.toMutableList().apply {
                    val existing = this[existingIndex]
                    this[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
                }
            } else {
                currentList + CartItem(
                    product = product,
                    selectedSize = size,
                    selectedMilk = milk,
                    selectedSugar = sugar,
                    quantity = quantity
                )
            }
        }
    }

    override fun updateCartQuantity(cartItemId: String, quantity: Int) {
        _cartItems.update { currentList ->
            if (quantity <= 0) {
                currentList.filterNot { it.internalId == cartItemId }
            } else {
                currentList.map {
                    if (it.internalId == cartItemId) it.copy(quantity = quantity) else it
                }
            }
        }
    }

    override fun removeFromCart(cartItemId: String) {
        _cartItems.update { currentList ->
            currentList.filterNot { it.internalId == cartItemId }
        }
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
    }

    override fun placeOrder(): ActiveOrder? {
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return null

        val totalQty = currentCart.sumOf { it.quantity }
        val subtotal = currentCart.sumOf { it.totalPrice }
        val deliveryFee = _storeMeta.value.deliveryFee
        val tax = subtotal * (_storeMeta.value.taxRatePercent / 100.0)
        val grandTotal = subtotal + deliveryFee + tax

        val summary = currentCart.map {
            "${it.quantity}x ${it.product?.name ?: it.coffeeItem?.name ?: "Item"}"
        }
        val orderCode = "#BK-${Random.nextInt(10000, 99999)}"
        val newOrder = ActiveOrder(
            orderId = orderCode,
            estimatedWait = if (_storeMeta.value.estimatedDeliveryTime.isNotBlank()) {
                _storeMeta.value.estimatedDeliveryTime
            } else {
                "20–30 minutes"
            },
            itemCount = totalQty,
            totalAmount = grandTotal,
            statusText = "Barista accepted your order!",
            itemsSummary = summary
        )

        _activeOrder.value = newOrder
        clearCart()
        return newOrder
    }
}
