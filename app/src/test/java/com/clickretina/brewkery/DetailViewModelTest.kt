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
import com.clickretina.brewkery.ui.screens.detail.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTestBrewkeryRepository

    private val sampleProduct = ProductItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        tagline = "Double shot espresso with velvety oat milk",
        description = "Rich espresso roast layered with velvety steamed milk",
        basePrice = 4.85,
        rating = 4.9,
        reviewCount = 312,
        imageUrl = "https://example.com/item1.jpg",
        badge = "BESTSELLER",
        ingredients = listOf("Signature Espresso", "Steamed Oat Milk"),
        sizes = listOf(
            ProductSizeOption("sz_small", "Tall (8 oz)", 0.0),
            ProductSizeOption("sz_medium", "Grande (12 oz)", 0.65),
            ProductSizeOption("sz_large", "Venti (16 oz)", 1.25)
        ),
        sugarLevels = listOf("0% Unsweetened", "50% Mild", "100% Standard Sweet"),
        milkOptions = listOf(
            ProductMilkOption("m_oat", "Oat Milk", 0.0),
            ProductMilkOption("m_almond", "Roasted Almond Milk", 0.50)
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTestBrewkeryRepository(sampleProduct)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load sets product and default customizations`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.product)
        assertEquals("Toasted Caramel Macchiato", state.product?.name)
        assertEquals("sz_small", state.selectedSize?.id)
        assertEquals("m_oat", state.selectedMilk?.id)
        assertEquals("0% Unsweetened", state.selectedSugar)
        assertEquals(1, state.quantity)
        assertEquals(4.85, state.unitPrice, 0.001)
        assertEquals(4.85, state.totalPrice, 0.001)
    }

    @Test
    fun `selecting size recalculates price dynamically`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        // Select Grande (+0.65)
        viewModel.onSizeSelected(sampleProduct.sizes[1])
        assertEquals(5.50, viewModel.uiState.value.unitPrice, 0.001)
        assertEquals(5.50, viewModel.uiState.value.totalPrice, 0.001)

        // Select Venti (+1.25)
        viewModel.onSizeSelected(sampleProduct.sizes[2])
        assertEquals(6.10, viewModel.uiState.value.unitPrice, 0.001)
        assertEquals(6.10, viewModel.uiState.value.totalPrice, 0.001)
    }

    @Test
    fun `selecting milk recalculates price dynamically`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        // Select Almond Milk (+0.50)
        viewModel.onMilkSelected(sampleProduct.milkOptions[1])
        assertEquals(5.35, viewModel.uiState.value.unitPrice, 0.001)
        assertEquals(5.35, viewModel.uiState.value.totalPrice, 0.001)

        // Also select Grande size (+0.65) -> 4.85 + 0.65 + 0.50 = 6.00
        viewModel.onSizeSelected(sampleProduct.sizes[1])
        assertEquals(6.00, viewModel.uiState.value.unitPrice, 0.001)
        assertEquals(6.00, viewModel.uiState.value.totalPrice, 0.001)
    }

    @Test
    fun `changing quantity recalculates total price`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        // Unit price = 4.85, qty = 2 -> 9.70
        viewModel.increaseQuantity()
        assertEquals(2, viewModel.uiState.value.quantity)
        assertEquals(9.70, viewModel.uiState.value.totalPrice, 0.001)

        viewModel.increaseQuantity()
        assertEquals(3, viewModel.uiState.value.quantity)
        assertEquals(14.55, viewModel.uiState.value.totalPrice, 0.001)

        viewModel.decreaseQuantity()
        assertEquals(2, viewModel.uiState.value.quantity)
        assertEquals(9.70, viewModel.uiState.value.totalPrice, 0.001)
    }

    @Test
    fun `decreaseQuantity cannot go below 1`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        viewModel.decreaseQuantity()
        viewModel.decreaseQuantity()
        assertEquals(1, viewModel.uiState.value.quantity)
    }

    @Test
    fun `addToCart adds configured item to repository`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        viewModel.onSizeSelected(sampleProduct.sizes[1]) // Grande +0.65
        viewModel.onMilkSelected(sampleProduct.milkOptions[1]) // Almond +0.50
        viewModel.onSugarSelected("50% Mild")
        viewModel.increaseQuantity() // qty = 2

        val added = viewModel.addToCart()
        advanceUntilIdle()

        assertTrue(added)
        assertTrue(viewModel.uiState.value.isAddedToCart)
        assertEquals(1, fakeRepository.cartItems.value.size)
        val cartItem = fakeRepository.cartItems.value.first()
        assertEquals("Grande (12 oz)", cartItem.selectedSize.label)
        assertEquals("Roasted Almond Milk", cartItem.selectedMilk.name)
        assertEquals("50% Mild", cartItem.selectedSugar)
        assertEquals(2, cartItem.quantity)
        assertEquals(6.00, cartItem.unitPrice, 0.001)
        assertEquals(12.00, cartItem.totalPrice, 0.001)
    }

    @Test
    fun `toggleFavorite updates favorite state`() = runTest {
        val viewModel = DetailViewModel(productId = 1, repository = fakeRepository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)
        viewModel.toggleFavorite()
        assertTrue(viewModel.uiState.value.isFavorite)
        viewModel.toggleFavorite()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    private class FakeTestBrewkeryRepository(
        private val product: ProductItem
    ) : BrewkeryRepository {
        private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
        override val cartItems: StateFlow<List<CartItem>> = _cart.asStateFlow()

        override val menuState: StateFlow<Resource<List<ProductItem>>> =
            MutableStateFlow(Resource.Success(listOf(product)))
        override val storeMeta: StateFlow<StoreMeta> = MutableStateFlow(StoreMeta())
        override val categories: StateFlow<List<Category>> = MutableStateFlow(emptyList())
        override val activeOrder: StateFlow<ActiveOrder?> = MutableStateFlow(null)

        override suspend fun getMenu(forceRefresh: Boolean): Resource<List<ProductItem>> =
            Resource.Success(listOf(product))

        override suspend fun getItemDetail(id: Int): Resource<ProductItem> =
            Resource.Success(product)

        override fun addToCart(
            product: ProductItem,
            size: ProductSizeOption,
            milk: ProductMilkOption,
            sugar: String,
            quantity: Int
        ) {
            _cart.value = _cart.value + CartItem(
                product = product,
                selectedSize = size,
                selectedMilk = milk,
                selectedSugar = sugar,
                quantity = quantity
            )
        }

        override fun updateCartQuantity(cartItemId: String, quantity: Int) {}
        override fun removeFromCart(cartItemId: String) {}
        override fun clearCart() { _cart.value = emptyList() }
        override fun placeOrder(): ActiveOrder? = null
    }
}
