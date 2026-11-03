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
import com.clickretina.brewkery.ui.screens.order.OrderStatusViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderStatusViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeActiveOrder = ActiveOrder(
        orderId = "#BK-84291",
        estimatedWait = "20–30 minutes",
        itemCount = 3,
        totalAmount = 18.25,
        statusText = "Barista accepted your order!",
        itemsSummary = listOf("2x Toasted Caramel Macchiato", "1x Golden Normandy Croissant")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observing active order populates state correctly`() = runTest {
        val fakeRepo = FakeOrderStatusBrewkeryRepository(fakeActiveOrder)
        val viewModel = OrderStatusViewModel(fakeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.order)
        assertEquals("#BK-84291", state.displayOrderId)
        assertEquals("20–30 minutes", state.displayWaitTime)
        assertEquals(3, state.displayItemCount)
        assertEquals(18.25, state.displayTotalAmount, 0.001)
        assertEquals("Barista accepted your order!", state.displayStatusText)
        assertEquals(2, state.displayItemsSummary.size)
        assertEquals("2x Toasted Caramel Macchiato", state.displayItemsSummary[0])
    }

    @Test
    fun `null active order falls back to safe default ticket values`() = runTest {
        val fakeRepo = FakeOrderStatusBrewkeryRepository(null)
        val viewModel = OrderStatusViewModel(fakeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("#BK-74921", state.displayOrderId)
        assertEquals("20–30 minutes", state.displayWaitTime)
        assertEquals(1, state.displayItemCount)
        assertEquals("Barista accepted your order!", state.displayStatusText)
    }

    private class FakeOrderStatusBrewkeryRepository(
        activeOrderInitial: ActiveOrder?
    ) : BrewkeryRepository {
        private val _order = MutableStateFlow(activeOrderInitial)
        override val activeOrder: StateFlow<ActiveOrder?> = _order.asStateFlow()

        override val cartItems: StateFlow<List<CartItem>> = MutableStateFlow(emptyList())
        override val menuState: StateFlow<Resource<List<ProductItem>>> = MutableStateFlow(Resource.Success(emptyList()))
        override val storeMeta: StateFlow<StoreMeta> = MutableStateFlow(StoreMeta())
        override val categories: StateFlow<List<Category>> = MutableStateFlow(emptyList())

        override suspend fun getMenu(forceRefresh: Boolean): Resource<List<ProductItem>> = Resource.Success(emptyList())
        override suspend fun getItemDetail(id: Int): Resource<ProductItem> = Resource.Error("Not implemented")
        override fun addToCart(product: ProductItem, size: ProductSizeOption, milk: ProductMilkOption, sugar: String, quantity: Int) {}
        override fun updateCartQuantity(cartItemId: String, quantity: Int) {}
        override fun removeFromCart(cartItemId: String) {}
        override fun clearCart() {}
        override fun placeOrder(): ActiveOrder? = null
    }
}
