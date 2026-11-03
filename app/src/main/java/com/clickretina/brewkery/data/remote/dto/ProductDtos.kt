package com.clickretina.brewkery.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponseDto(
    @SerialName("meta")
    val meta: StoreMetaDto? = null,
    @SerialName("categories")
    val categories: List<CategoryDto> = emptyList(),
    @SerialName("items")
    val items: List<ProductItemDto> = emptyList()
)

@Serializable
data class StoreMetaDto(
    @SerialName("app")
    val app: String? = "Brewkery",
    @SerialName("version")
    val version: String? = "1.0.0",
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("currency")
    val currency: String? = "USD",
    @SerialName("currency_symbol")
    val currencySymbol: String? = "$",
    @SerialName("delivery_fee")
    val deliveryFee: Double? = 2.50,
    @SerialName("tax_rate_percent")
    val taxRatePercent: Double? = 8.0,
    @SerialName("estimated_delivery_time")
    val estimatedDeliveryTime: String? = "20 - 30 mins"
)

@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("item_count")
    val itemCount: Int? = 0
)

@Serializable
data class ProductItemDto(
    @SerialName("id")
    val id: Int,
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("name")
    val name: String,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("base_price")
    val basePrice: Double,
    @SerialName("rating")
    val rating: Double? = 0.0,
    @SerialName("review_count")
    val reviewCount: Int? = 0,
    @SerialName("prep_time")
    val prepTime: String? = null,
    @SerialName("calories")
    val calories: Int? = null,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("badge")
    val badge: String? = null,
    @SerialName("ingredients")
    val ingredients: List<String> = emptyList(),
    @SerialName("customizations")
    val customizations: CustomizationsDto? = null
)

@Serializable
data class CustomizationsDto(
    @SerialName("sizes")
    val sizes: List<SizeOptionDto> = emptyList(),
    @SerialName("sugar_levels")
    val sugarLevels: List<String> = emptyList(),
    @SerialName("milk_options")
    val milkOptions: List<MilkOptionDto> = emptyList()
)

@Serializable
data class SizeOptionDto(
    @SerialName("id")
    val id: String,
    @SerialName("label")
    val label: String,
    @SerialName("extra_price")
    val extraPrice: Double = 0.0
)

@Serializable
data class MilkOptionDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("extra_price")
    val extraPrice: Double = 0.0
)

