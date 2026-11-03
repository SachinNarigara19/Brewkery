package com.clickretina.brewkery.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.CoffeeRepository
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val coffeeId: String,
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCoffee()
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                val totalCount = items.sumOf { it.quantity }
                _uiState.update { it.copy(cartCount = totalCount) }
            }
        }
    }

    private fun loadCoffee() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getCoffeeById(coffeeId)
            result.onSuccess { item ->
                _uiState.update { it.copy(isLoading = false, coffee = item) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to find coffee item"
                    )
                }
            }
        }
    }

    fun onSizeSelected(size: CoffeeSize) {
        _uiState.update { it.copy(selectedSize = size) }
    }

    fun onSugarSelected(sugar: SugarLevel) {
        _uiState.update { it.copy(selectedSugar = sugar) }
    }

    fun increaseQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1) }
    }

    fun decreaseQuantity() {
        _uiState.update {
            val next = (it.quantity - 1).coerceAtLeast(1)
            it.copy(quantity = next)
        }
    }

    fun addToCart(): Boolean {
        val coffee = _uiState.value.coffee ?: return false
        repository.addToCart(
            coffeeItem = coffee,
            size = _uiState.value.selectedSize,
            sugar = _uiState.value.selectedSugar,
            quantity = _uiState.value.quantity
        )
        return true
    }

    class Factory(
        private val coffeeId: String,
        private val repository: CoffeeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(coffeeId, repository) as T
        }
    }
}

