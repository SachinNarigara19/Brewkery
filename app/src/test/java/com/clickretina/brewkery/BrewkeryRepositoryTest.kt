package com.clickretina.brewkery

import com.clickretina.brewkery.data.remote.BrewkeryApiService
import com.clickretina.brewkery.data.remote.RetrofitClient
import com.clickretina.brewkery.data.remote.dto.CategoryDto
import com.clickretina.brewkery.data.remote.dto.CustomizationsDto
import com.clickretina.brewkery.data.remote.dto.MenuResponseDto
import com.clickretina.brewkery.data.remote.dto.MilkOptionDto
import com.clickretina.brewkery.data.remote.dto.ProductItemDto
import com.clickretina.brewkery.data.remote.dto.SizeOptionDto
import com.clickretina.brewkery.data.remote.dto.StoreMetaDto
import com.clickretina.brewkery.data.repository.BrewkeryRepositoryImpl
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BrewkeryRepositoryTest {

    private lateinit var mockApiService: BrewkeryApiService
    private lateinit var repository: BrewkeryRepositoryImpl

    private val sampleMenuResponse = MenuResponseDto(
        meta = StoreMetaDto(
            app = "Brewkery",
            version = "1.0.0",
            tagline = "Artisanal Coffee",
            deliveryFee = 2.50,
            taxRatePercent = 8.0,
            estimatedDeliveryTime = "20 - 30 mins"
        ),
        categories = listOf(
            CategoryDto(id = "cat_hot_coffee", name = "Hot Coffee", icon = "☕", itemCount = 1)
        ),
        items = listOf(
            ProductItemDto(
                id = 1,
                categoryId = "cat_hot_coffee",
                name = "Toasted Caramel Macchiato",
                tagline = "Double shot espresso",
                description = "Rich espresso roast",
                basePrice = 4.85,
                rating = 4.9,
                reviewCount = 312,
                prepTime = "5-7 mins",
                calories = 240,
                imageUrl = "https://images.unsplash.com/sample.jpg",
                badge = "BESTSELLER",
                ingredients = listOf("Signature Espresso", "Steamed Oat Milk"),
                customizations = CustomizationsDto(
                    sizes = listOf(
                        SizeOptionDto(id = "sz_small", label = "Tall (8 oz)", extraPrice = 0.0),
                        SizeOptionDto(id = "sz_medium", label = "Grande (12 oz)", extraPrice = 0.65)
                    ),
                    sugarLevels = listOf("0% Unsweetened", "50% Mild", "100% Standard Sweet"),
                    milkOptions = listOf(
                        MilkOptionDto(id = "m_oat", name = "Oat Milk (Barista Blend)", extraPrice = 0.0),
                        MilkOptionDto(id = "m_almond", name = "Roasted Almond Milk", extraPrice = 0.50)
                    )
                )
            )
        )
    )

    @Before
    fun setup() {
        mockApiService = object : BrewkeryApiService {
            override suspend fun getMenu(): Response<MenuResponseDto> {
                return Response.success(sampleMenuResponse)
            }

            override suspend fun getItemById(id: Int): Response<ProductItemDto> {
                val item = sampleMenuResponse.items.firstOrNull { it.id == id }
                return if (item != null) {
                    Response.success(item)
                } else {
                    Response.error(404, "Item not found".toResponseBody("text/plain".toMediaTypeOrNull()))
                }
            }
        }

        repository = BrewkeryRepositoryImpl(
            apiService = mockApiService,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun getMenu_successfullyParsesAndCachesData() = runTest {
        val result = repository.getMenu(forceRefresh = true)
        assertTrue(result is Resource.Success)
        val items = (result as Resource.Success).data
        assertEquals(1, items.size)
        assertEquals("Toasted Caramel Macchiato", items.first().name)

        // Store meta and categories should be updated
        assertEquals("Brewkery", repository.storeMeta.value.app)
        assertEquals(2.50, repository.storeMeta.value.deliveryFee, 0.01)
        assertEquals(1, repository.categories.value.size)
        assertEquals("cat_hot_coffee", repository.categories.value.first().id)
    }

    @Test
    fun getItemDetail_returnsItemSuccessfully() = runTest {
        val result = repository.getItemDetail(1)
        assertTrue(result is Resource.Success)
        val item = (result as Resource.Success).data
        assertEquals(1, item.id)
        assertEquals(4.85, item.basePrice, 0.01)
        assertEquals(2, item.sizes.size)
        assertEquals(2, item.milkOptions.size)
    }

    @Test
    fun addToCart_calculatesCustomizationPricesCorrectly() = runTest {
        val menuResult = repository.getMenu(forceRefresh = true)
        val item = (menuResult as Resource.Success).data.first()

        val sizeGrande = ProductSizeOption(id = "sz_medium", label = "Grande (12 oz)", extraPrice = 0.65)
        val milkAlmond = ProductMilkOption(id = "m_almond", name = "Roasted Almond Milk", extraPrice = 0.50)

        repository.addToCart(
            product = item,
            size = sizeGrande,
            milk = milkAlmond,
            sugar = "50% Mild",
            quantity = 2
        )

        val cart = repository.cartItems.value
        assertEquals(1, cart.size)
        val cartItem = cart.first()
        assertEquals(2, cartItem.quantity)
        // unit price = 4.85 + 0.65 + 0.50 = 6.00
        assertEquals(6.00, cartItem.unitPrice, 0.001)
        // total price = 6.00 * 2 = 12.00
        assertEquals(12.00, cartItem.totalPrice, 0.001)
    }

    @Test
    fun placeOrder_calculatesTotalAndClearsCart() = runTest {
        val menuResult = repository.getMenu(forceRefresh = true)
        val item = (menuResult as Resource.Success).data.first()

        repository.addToCart(
            product = item,
            size = ProductSizeOption("sz_small", "Tall", 0.0),
            milk = ProductMilkOption("m_oat", "Oat", 0.0),
            sugar = "50% Mild",
            quantity = 1
        )

        val order = repository.placeOrder()
        assertNotNull(order)
        assertTrue(order!!.orderId.startsWith("#BK-"))
        assertEquals(1, order.itemCount)

        // Subtotal = 4.85, Delivery = 2.50, Tax = 4.85 * 0.08 = 0.388 => Total ~ 7.738
        val expectedTotal = 4.85 + 2.50 + (4.85 * 0.08)
        assertEquals(expectedTotal, order.totalAmount, 0.01)

        // Cart should be empty after order
        assertTrue(repository.cartItems.value.isEmpty())
        assertEquals(order, repository.activeOrder.value)
    }

    @Test
    fun getMenu_handlesHttpErrorGracefully() = runTest {
        val errorApiService = object : BrewkeryApiService {
            override suspend fun getMenu(): Response<MenuResponseDto> {
                return Response.error(500, "Internal Server Error".toResponseBody("text/plain".toMediaTypeOrNull()))
            }

            override suspend fun getItemById(id: Int): Response<ProductItemDto> {
                return Response.error(500, "Internal Server Error".toResponseBody("text/plain".toMediaTypeOrNull()))
            }
        }

        val errRepo = BrewkeryRepositoryImpl(errorApiService, Dispatchers.Unconfined)
        val result = errRepo.getMenu(forceRefresh = true)
        assertTrue(result is Resource.Error)
        val errorMsg = (result as Resource.Error).message
        assertTrue(errorMsg.contains("500"))
    }
}

