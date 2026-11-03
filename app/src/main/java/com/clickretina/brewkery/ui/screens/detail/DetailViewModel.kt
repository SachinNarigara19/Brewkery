package com.clickretina.brewkery.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productId: Int,
    private val repository: BrewkeryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadProductDetail()
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

    fun loadProductDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getItemDetail(productId)) {
                is Resource.Success -> {
                    val product = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            selectedSize = product.sizes.firstOrNull(),
                            selectedMilk = product.milkOptions.firstOrNull(),
                            selectedSugar = product.sugarLevels.firstOrNull().orEmpty(),
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onSizeSelected(size: ProductSizeOption) {
        _uiState.update { it.copy(selectedSize = size) }
    }

    fun onMilkSelected(milk: ProductMilkOption) {
        _uiState.update { it.copy(selectedMilk = milk) }
    }

    fun onSugarSelected(sugar: String) {
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

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun addToCart(): Boolean {
        val currentState = _uiState.value
        val product = currentState.product ?: return false
        val size = currentState.selectedSize ?: product.sizes.firstOrNull() ?: ProductSizeOption("default", "Regular")
        val milk = currentState.selectedMilk ?: product.milkOptions.firstOrNull() ?: ProductMilkOption("default", "Standard")
        val sugar = currentState.selectedSugar.ifBlank { product.sugarLevels.firstOrNull().orEmpty() }

        repository.addToCart(
            product = product,
            size = size,
            milk = milk,
            sugar = sugar,
            quantity = currentState.quantity
        )

        _uiState.update { it.copy(isAddedToCart = true) }
        return true
    }

    fun resetAddedToCart() {
        _uiState.update { it.copy(isAddedToCart = false) }
    }

    class Factory(
        private val productId: Int,
        private val repository: BrewkeryRepository
    ) : ViewModelProvider.Factory {

        constructor(productIdStr: String, repository: BrewkeryRepository) : this(
            productIdStr.toIntOrNull() ?: 1,
            repository
        )

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(productId, repository) as T
        }
    }
}
