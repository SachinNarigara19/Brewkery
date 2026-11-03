package com.clickretina.brewkery.domain.model

import com.clickretina.brewkery.data.model.CoffeeItem
import java.util.UUID

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

data class CartItem(
    val internalId: String = UUID.randomUUID().toString(),
    val product: ProductItem? = null,
    val selectedSize: ProductSizeOption = ProductSizeOption(id = "default", label = "Regular"),
    val selectedMilk: ProductMilkOption = ProductMilkOption(id = "default", name = "Standard"),
    val selectedSugar: String = "Normal",
    val quantity: Int = 1,
    // Compatibility fields for placeholder screens until UI migration
    val coffeeItem: CoffeeItem? = null,
    val legacySize: CoffeeSize = CoffeeSize.REGULAR,
    val legacySugar: SugarLevel = SugarLevel.NORMAL
) {
    val unitPrice: Double
        get() = (product?.basePrice ?: coffeeItem?.price ?: 0.0) +
                selectedSize.extraPrice +
                selectedMilk.extraPrice

    val totalPrice: Double
        get() = unitPrice * quantity
}
