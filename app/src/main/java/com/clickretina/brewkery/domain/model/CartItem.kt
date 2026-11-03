package com.clickretina.brewkery.domain.model

import com.clickretina.brewkery.data.model.CoffeeItem

data class CartItem(
    val coffeeItem: CoffeeItem,
    val quantity: Int = 1,
    val selectedSize: CoffeeSize = CoffeeSize.REGULAR,
    val selectedSugar: SugarLevel = SugarLevel.NORMAL
) {
    val totalPrice: Double
        get() = (coffeeItem.price + selectedSize.extraPrice) * quantity
}

enum class CoffeeSize(val label: String, val extraPrice: Double) {
    SMALL("Small", 0.0),
    REGULAR("Regular", 0.50),
    LARGE("Large", 1.00)
}

enum class SugarLevel(val label: String) {
    NO_SUGAR("0% (No Sugar)"),
    LOW("25% (Low)"),
    NORMAL("50% (Normal)"),
    SWEET("100% (Sweet)")
}
