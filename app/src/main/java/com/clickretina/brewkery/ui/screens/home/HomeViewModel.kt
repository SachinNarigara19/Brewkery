package com.clickretina.brewkery.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: BrewkeryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRepositoryStreams()
        loadMenu(forceRefresh = false)
    }

    private fun observeRepositoryStreams() {
        // Observe Menu Resource State
        viewModelScope.launch {
            repository.menuState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                products = resource.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }

        // Observe Store Meta
        viewModelScope.launch {
            repository.storeMeta.collect { meta ->
                _uiState.update { it.copy(storeMeta = meta) }
            }
        }

        // Observe Categories
        viewModelScope.launch {
            repository.categories.collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }

        // Observe Cart Items (Single Source of Truth)
        viewModelScope.launch {
            repository.cartItems.collect { items ->
                val count = items.sumOf { it.quantity }
                val subtotal = items.sumOf { it.totalPrice }
                _uiState.update {
                    it.copy(
                        cartCount = count,
                        cartSubtotal = subtotal
                    )
                }
            }
        }

        // Observe Active Order for Dynamic Banner
        viewModelScope.launch {
            repository.activeOrder.collect { order ->
                _uiState.update { it.copy(activeOrder = order) }
            }
        }
    }

    fun loadMenu(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getMenu(forceRefresh = forceRefresh)
        }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onRetry() {
        loadMenu(forceRefresh = true)
    }

    class Factory(private val repository: BrewkeryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
