package com.clickretina.brewkery

import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.Resource
import com.clickretina.brewkery.domain.model.StoreMeta
import com.clickretina.brewkery.domain.util.PriceCalculator
import com.clickretina.brewkery.ui.screens.cart.CartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeCartTestBrewkeryRepository
    private val calculator = PriceCalculator(defaultDeliveryFee = 2.50, defaultTaxRatePercent = 8.0)

    private val product1 = ProductItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        basePrice = 4.85,
        imageUrl = "https://example.com/1.jpg"
    )

    private val cartItem1 = CartItem(
        internalId = "item_1",
        product = product1,
        selectedSize = ProductSizeOption("sz_medium", "Grande", 0.65),
        selectedMilk = ProductMilkOption("m_oat", "Oat Milk", 0.0),
        quantity = 2
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeCartTestBrewkeryRepository(listOf(cartItem1))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial cart state calculates bill breakdown correctly`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        // Unit price = 4.85 + 0.65 = 5.50 * 2 = 11.00
        assertEquals(11.00, state.subtotal, 0.001)
        assertEquals(2.50, state.deliveryFee, 0.001)
        assertEquals(0.88, state.estimatedTax, 0.001) // 11.00 * 8% = 0.88
        assertEquals(14.38, state.totalPayable, 0.001) // 11.00 + 2.50 + 0.88
        assertEquals(2, state.itemCount)
    }

    @Test
    fun `updateQuantity modifies item and recalculates totals`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        // Increase qty from 2 to 3
        viewModel.updateQuantity("item_1", 3)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.items.first().quantity)
        // 5.50 * 3 = 16.50
        assertEquals(16.50, state.subtotal, 0.001)
        assertEquals(2.50, state.deliveryFee, 0.001)
        assertEquals(1.32, state.estimatedTax, 0.001) // 16.50 * 8% = 1.32
        assertEquals(20.32, state.totalPayable, 0.001) // 16.50 + 2.50 + 1.32
    }

    @Test
    fun `updateQuantity with zero removes item`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        viewModel.updateQuantity("item_1", 0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.items.isEmpty())
        assertEquals(0.0, state.subtotal, 0.001)
        assertEquals(0.0, state.deliveryFee, 0.001)
        assertEquals(0.0, state.totalPayable, 0.001)
    }

    @Test
    fun `removeItem removes specific item`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        viewModel.removeItem("item_1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `clearCart removes all items`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        viewModel.clearCart()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `placeOrder creates active order and clears cart`() = runTest {
        val viewModel = CartViewModel(fakeRepository, calculator)
        advanceUntilIdle()

        val order = viewModel.placeOrder()
        advanceUntilIdle()

        assertNotNull(order)
        assertTrue(order?.orderId?.startsWith("#BK-") == true)
        assertEquals(14.38, order?.totalAmount ?: 0.0, 0.001)
        assertTrue(viewModel.uiState.value.isOrderPlaced)
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    private class FakeCartTestBrewkeryRepository(
        initialItems: List<CartItem>
    ) : BrewkeryRepository {
        private val _cart = MutableStateFlow(initialItems)
        override val cartItems: StateFlow<List<CartItem>> = _cart.asStateFlow()

        private val _meta = MutableStateFlow(
            StoreMeta(
                deliveryFee = 2.50,
                taxRatePercent = 8.0,
                estimatedDeliveryTime = "20 - 30 mins"
            )
        )
        override val storeMeta: StateFlow<StoreMeta> = _meta.asStateFlow()

        override val menuState: StateFlow<Resource<List<ProductItem>>> =
            MutableStateFlow(Resource.Success(emptyList()))
        override val categories: StateFlow<List<Category>> = MutableStateFlow(emptyList())
        private val _activeOrder = MutableStateFlow<ActiveOrder?>(null)
        override val activeOrder: StateFlow<ActiveOrder?> = _activeOrder.asStateFlow()

        override suspend fun getMenu(forceRefresh: Boolean): Resource<List<ProductItem>> =
            Resource.Success(emptyList())

        override suspend fun getItemDetail(id: Int): Resource<ProductItem> =
            Resource.Error("Not implemented")

        override fun addToCart(
            product: ProductItem,
            size: ProductSizeOption,
            milk: ProductMilkOption,
            sugar: String,
            quantity: Int
        ) {
            _cart.update {
                it + CartItem(
                    product = product,
                    selectedSize = size,
                    selectedMilk = milk,
                    selectedSugar = sugar,
                    quantity = quantity
                )
            }
        }

        override fun updateCartQuantity(cartItemId: String, quantity: Int) {
            _cart.update { list ->
                if (quantity <= 0) {
                    list.filterNot { it.internalId == cartItemId }
                } else {
                    list.map { if (it.internalId == cartItemId) it.copy(quantity = quantity) else it }
                }
            }
        }

        override fun removeFromCart(cartItemId: String) {
            _cart.update { it.filterNot { item -> item.internalId == cartItemId } }
        }

        override fun clearCart() {
            _cart.value = emptyList()
        }

        override fun placeOrder(): ActiveOrder? {
            val items = _cart.value
            if (items.isEmpty()) return null
            val subtotal = items.sumOf { it.totalPrice }
            val fee = _meta.value.deliveryFee
            val tax = subtotal * (_meta.value.taxRatePercent / 100.0)
            val total = subtotal + fee + tax
            val order = ActiveOrder(
                orderId = "#BK-99999",
                estimatedWait = _meta.value.estimatedDeliveryTime,
                itemCount = items.sumOf { it.quantity },
                totalAmount = total
            )
            _activeOrder.value = order
            clearCart()
            return order
        }
    }
}

