package com.clickretina.brewkery.ui.screens.home

import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.data.model.CoffeeItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedCategory: CoffeeCategory = CoffeeCategory.ALL,
    val searchQuery: String = "",
    val coffees: List<CoffeeItem> = emptyList(),
    val featuredCoffees: List<CoffeeItem> = emptyList(),
    val cartCount: Int = 0,
    val errorMessage: String? = null
) {
    val filteredCoffees: List<CoffeeItem>
        get() = coffees.filter {
            searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.subtitle.contains(searchQuery, ignoreCase = true)
        }
}

