package com.clickretina.brewkery.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.util.PriceCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: BrewkeryRepository,
    private val priceCalculator: PriceCalculator = PriceCalculator.DEFAULT
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCartAndStoreMeta()
    }

    private fun observeCartAndStoreMeta() {
        viewModelScope.launch {
            combine(repository.cartItems, repository.storeMeta) { items, meta ->
                val bill = priceCalculator.calculateBill(
                    items = items,
                    deliveryFee = meta.deliveryFee,
                    taxRatePercent = meta.taxRatePercent
                )
                CartUiState(
                    items = items,
                    storeDeliveryFee = meta.deliveryFee,
                    taxRatePercent = meta.taxRatePercent,
                    bill = bill,
                    placedOrder = _uiState.value.placedOrder
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            repository.removeFromCart(cartItemId)
        } else {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeItem(cartItemId: String) {
        repository.removeFromCart(cartItemId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun placeOrder(): ActiveOrder? {
        val newOrder = repository.placeOrder()
        if (newOrder != null) {
            _uiState.update { it.copy(placedOrder = newOrder) }
        }
        return newOrder
    }

    fun resetOrderState() {
        _uiState.update { it.copy(placedOrder = null) }
    }

    class Factory(
        private val repository: BrewkeryRepository,
        private val priceCalculator: PriceCalculator = PriceCalculator.DEFAULT
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartViewModel(repository, priceCalculator) as T
        }
    }
}
