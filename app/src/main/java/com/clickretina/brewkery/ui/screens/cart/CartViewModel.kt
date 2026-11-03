package com.clickretina.brewkery.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.CoffeeRepository
import com.clickretina.brewkery.domain.model.CoffeeSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    fun updateQuantity(coffeeId: String, size: CoffeeSize, quantity: Int) {
        repository.updateCartItemQuantity(coffeeId, size, quantity)
    }

    fun removeItem(coffeeId: String, size: CoffeeSize) {
        repository.removeFromCart(coffeeId, size)
    }

    fun checkout() {
        repository.clearCart()
        _uiState.update { it.copy(isOrderPlaced = true) }
    }

    fun resetOrderState() {
        _uiState.update { it.copy(isOrderPlaced = false) }
    }

    class Factory(private val repository: CoffeeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartViewModel(repository) as T
        }
    }
}
