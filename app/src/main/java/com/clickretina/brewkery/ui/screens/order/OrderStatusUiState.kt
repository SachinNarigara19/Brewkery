package com.clickretina.brewkery.ui.screens.order

import com.clickretina.brewkery.domain.model.ActiveOrder

data class OrderStatusUiState(
    val order: ActiveOrder? = null,
    val isLoading: Boolean = false
) {
    val displayOrderId: String
        get() = order?.orderId ?: "#BK-74921"

    val displayWaitTime: String
        get() = order?.estimatedWait?.ifBlank { "20–30 minutes" } ?: "20–30 minutes"

    val displayItemCount: Int
        get() = order?.itemCount ?: 1

    val displayStatusText: String
        get() = order?.statusText?.ifBlank { "Barista accepted your order!" } ?: "Barista accepted your order!"

    val displayTotalAmount: Double
        get() = order?.totalAmount ?: 0.0

    val displayItemsSummary: List<String>
        get() = order?.itemsSummary ?: emptyList()
}
