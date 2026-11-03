package com.clickretina.brewkery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CoffeeItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String,
    val category: CoffeeCategory,
    val roastLevel: String = "Medium Roast",
    val origin: String = "Ethiopia / Colombia",
    val isFeatured: Boolean = false
)
