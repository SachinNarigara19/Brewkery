package com.clickretina.brewkery.ui.screens.home

import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.StoreMeta

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: String = CATEGORY_ALL,
    val storeMeta: StoreMeta = StoreMeta(),
    val categories: List<Category> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val cartCount: Int = 0,
    val cartSubtotal: Double = 0.0,
    val activeOrder: ActiveOrder? = null
) {
    val filteredProducts: List<ProductItem>
        get() = products.filter { item ->
            val matchesCategory = selectedCategoryId == CATEGORY_ALL || item.categoryId == selectedCategoryId
            val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.tagline.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }

    companion object {
        const val CATEGORY_ALL = "ALL"
    }
}
