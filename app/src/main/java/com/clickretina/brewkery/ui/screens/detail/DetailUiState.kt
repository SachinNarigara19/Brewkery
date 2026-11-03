package com.clickretina.brewkery.ui.screens.detail

import com.clickretina.brewkery.data.model.CoffeeItem
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel

data class DetailUiState(
    val isLoading: Boolean = true,
    val coffee: CoffeeItem? = null,
    val selectedSize: CoffeeSize = CoffeeSize.REGULAR,
    val selectedSugar: SugarLevel = SugarLevel.NORMAL,
    val quantity: Int = 1,
    val cartCount: Int = 0,
    val errorMessage: String? = null
) {
    val currentTotalPrice: Double
        get() {
            val base = coffee?.price ?: 0.0
            return (base + selectedSize.extraPrice) * quantity
        }
}

