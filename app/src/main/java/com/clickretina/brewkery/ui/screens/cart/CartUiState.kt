package com.clickretina.brewkery.ui.screens.cart

import com.clickretina.brewkery.domain.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isOrderPlaced: Boolean = false
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val deliveryFee: Double
        get() = if (items.isEmpty()) 0.0 else 2.50

    val grandTotal: Double
        get() = subtotal + deliveryFee
}
