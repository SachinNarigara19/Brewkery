package com.clickretina.brewkery.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CoffeeCategory(val displayName: String) {
    ALL("All"),
    HOT_COFFEE("Hot Coffee"),
    ICED_COFFEE("Iced Coffee"),
    ESPRESSO("Espresso"),
    SPECIALTY("Specialty"),
    BAKERY("Bakery")
}

