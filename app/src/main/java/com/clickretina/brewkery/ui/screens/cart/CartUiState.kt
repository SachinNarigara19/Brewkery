package com.clickretina.brewkery.ui.screens.cart

import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.util.BillBreakdown

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val storeDeliveryFee: Double = 2.50,
    val taxRatePercent: Double = 8.0,
    val bill: BillBreakdown = BillBreakdown(),
    val isPlacingOrder: Boolean = false,
    val placedOrder: ActiveOrder? = null
) {
    val subtotal: Double
        get() = bill.subtotal

    val deliveryFee: Double
        get() = bill.deliveryFee

    val estimatedTax: Double
        get() = bill.estimatedTax

    val totalPayable: Double
        get() = bill.totalPayable

    val itemCount: Int
        get() = bill.itemCount

    val isOrderPlaced: Boolean
        get() = placedOrder != null
}
