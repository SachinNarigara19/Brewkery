package com.clickretina.brewkery.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.data.model.CoffeeItem
import com.clickretina.brewkery.data.repository.CoffeeRepository
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeCart()
        loadCoffees(CoffeeCategory.ALL)
    }

    private fun observeCart() {
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                val totalCount = items.sumOf { it.quantity }
                _uiState.update { it.copy(cartCount = totalCount) }
            }
        }
    }

    fun loadCoffees(category: CoffeeCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category, errorMessage = null) }
            val result = repository.getCoffees(category)
            result.onSuccess { list ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        coffees = list,
                        featuredCoffees = list.filter { it.isFeatured }
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load menu"
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: CoffeeCategory) {
        if (_uiState.value.selectedCategory != category) {
            loadCoffees(category)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun quickAddToCart(coffee: CoffeeItem) {
        repository.addToCart(
            coffeeItem = coffee,
            size = CoffeeSize.REGULAR,
            sugar = SugarLevel.NORMAL,
            quantity = 1
        )
    }

    class Factory(private val repository: CoffeeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
