package com.clickretina.brewkery.ui.screens.detail

import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption

data class DetailUiState(
    val isLoading: Boolean = true,
    val product: ProductItem? = null,
    val selectedSize: ProductSizeOption? = null,
    val selectedMilk: ProductMilkOption? = null,
    val selectedSugar: String = "",
    val quantity: Int = 1,
    val isFavorite: Boolean = false,
    val cartCount: Int = 0,
    val errorMessage: String? = null,
    val isAddedToCart: Boolean = false
) {
    /**
     * Unit price calculated dynamically as base price + size extra price + milk/spread extra price.
     */
    val unitPrice: Double
        get() {
            val base = product?.basePrice ?: 0.0
            val sizeExtra = selectedSize?.extraPrice ?: 0.0
            val milkExtra = selectedMilk?.extraPrice ?: 0.0
            return base + sizeExtra + milkExtra
        }

    /**
     * Total price calculated dynamically as Unit Price * Quantity.
     */
    val totalPrice: Double
        get() = unitPrice * quantity
}
