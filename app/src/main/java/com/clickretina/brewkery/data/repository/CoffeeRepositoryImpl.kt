package com.clickretina.brewkery.data.repository

import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.data.model.CoffeeItem
import com.clickretina.brewkery.data.remote.BrewkeryApiService
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class CoffeeRepositoryImpl(
    private val apiService: BrewkeryApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoffeeRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val localCoffeeCatalog = listOf(
        CoffeeItem(
            id = "c1",
            name = "Caramel Macchiato",
            subtitle = "Rich espresso with caramel drizzle",
            description = "Freshly steamed milk with vanilla-flavored syrup marked with espresso and topped with a caramel drizzle for an oh-so-sweet finish.",
            price = 4.85,
            rating = 4.9,
            reviewCount = 342,
            imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.HOT_COFFEE,
            roastLevel = "Medium Roast",
            origin = "Guatemala & Colombia",
            isFeatured = true
        ),
        CoffeeItem(
            id = "c2",
            name = "Vanilla Cold Brew",
            subtitle = "Slow-steeped smooth iced blend",
            description = "Handcrafted in small batches daily, slow-steeped in cool water for 20 hours, topped with a delicate splash of house-made vanilla cream.",
            price = 5.20,
            rating = 4.8,
            reviewCount = 289,
            imageUrl = "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.ICED_COFFEE,
            roastLevel = "Dark Roast",
            origin = "Sumatra & Brazil",
            isFeatured = true
        ),
        CoffeeItem(
            id = "c3",
            name = "Doppio Espresso",
            subtitle = "Double shot of pure crema essence",
            description = "Two shots of intense, full-bodied espresso with a velvety amber crema. Bold, complex notes of dark cocoa and roasted hazelnuts.",
            price = 3.60,
            rating = 4.7,
            reviewCount = 195,
            imageUrl = "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.ESPRESSO,
            roastLevel = "Dark Italian Roast",
            origin = "Ethiopian Yirgacheffe",
            isFeatured = false
        ),
        CoffeeItem(
            id = "c4",
            name = "Cinnamon Oat Flat White",
            subtitle = "Velvety microfoam over ristretto",
            description = "Expertly poured ristretto shots fused with silky steamed oat milk, finished with a subtle dust of Ceylon cinnamon.",
            price = 5.10,
            rating = 4.9,
            reviewCount = 412,
            imageUrl = "https://images.unsplash.com/photo-1534778101976-62847782c213?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.HOT_COFFEE,
            roastLevel = "Medium-Light Roast",
            origin = "Costa Rica Tarrazu",
            isFeatured = true
        ),
        CoffeeItem(
            id = "c5",
            name = "Brown Sugar Boba Latte",
            subtitle = "Specialty cold brew infused latte",
            description = "Artisanal brown sugar syrup swirled with organic milk and dark roasted espresso, served over slow-melt crystalline ice spheres.",
            price = 5.75,
            rating = 4.6,
            reviewCount = 188,
            imageUrl = "https://images.unsplash.com/photo-1541167760496-1628856ab772?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.SPECIALTY,
            roastLevel = "Medium Roast",
            origin = "Kenya Nyeri",
            isFeatured = false
        ),
        CoffeeItem(
            id = "c6",
            name = "Artisan Almond Croissant",
            subtitle = "Flaky butter pastry with frangipane",
            description = "Twice-baked butter croissant filled with velvety almond cream and topped with toasted sliced almonds and powdered sugar.",
            price = 4.25,
            rating = 4.9,
            reviewCount = 520,
            imageUrl = "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800&q=80",
            category = CoffeeCategory.BAKERY,
            roastLevel = "Fresh Daily",
            origin = "Brewkery In-House Bakery",
            isFeatured = true
        )
    )

    override suspend fun getCoffees(category: CoffeeCategory): Result<List<CoffeeItem>> =
        withContext(ioDispatcher) {
            runCatching {
                try {
                    val query = if (category == CoffeeCategory.ALL) null else category.name
                    val remote = apiService.getCoffees(query)
                    if (remote.isNotEmpty()) remote else filterLocal(category)
                } catch (e: Exception) {
                    // Graceful fallback to rich local catalog for offline / mock testing
                    filterLocal(category)
                }
            }
        }

    private fun filterLocal(category: CoffeeCategory): List<CoffeeItem> {
        return if (category == CoffeeCategory.ALL) {
            localCoffeeCatalog
        } else {
            localCoffeeCatalog.filter { it.category == category }
        }
    }

    override suspend fun getCoffeeById(id: String): Result<CoffeeItem> =
        withContext(ioDispatcher) {
            runCatching {
                try {
                    apiService.getCoffeeById(id)
                } catch (e: Exception) {
                    localCoffeeCatalog.firstOrNull { it.id == id }
                        ?: throw NoSuchElementException("Coffee with id $id not found")
                }
            }
        }

    override fun addToCart(
        coffeeItem: CoffeeItem,
        size: CoffeeSize,
        sugar: SugarLevel,
        quantity: Int
    ) {
        _cartItems.update { current ->
            val existingIndex = current.indexOfFirst {
                it.coffeeItem.id == coffeeItem.id && it.selectedSize == size && it.selectedSugar == sugar
            }
            if (existingIndex >= 0) {
                current.toMutableList().apply {
                    val existing = this[existingIndex]
                    this[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
                }
            } else {
                current + CartItem(
                    coffeeItem = coffeeItem,
                    quantity = quantity,
                    selectedSize = size,
                    selectedSugar = sugar
                )
            }
        }
    }

    override fun updateCartItemQuantity(coffeeId: String, size: CoffeeSize, quantity: Int) {
        _cartItems.update { current ->
            if (quantity <= 0) {
                current.filterNot { it.coffeeItem.id == coffeeId && it.selectedSize == size }
            } else {
                current.map {
                    if (it.coffeeItem.id == coffeeId && it.selectedSize == size) {
                        it.copy(quantity = quantity)
                    } else {
                        it
                    }
                }
            }
        }
    }

    override fun removeFromCart(coffeeId: String, size: CoffeeSize) {
        _cartItems.update { current ->
            current.filterNot { it.coffeeItem.id == coffeeId && it.selectedSize == size }
        }
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
    }
}
