package com.clickretina.brewkery.domain.model

data class StoreMeta(
    val app: String = "Brewkery",
    val version: String = "1.0.0",
    val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    val currency: String = "USD",
    val currencySymbol: String = "$",
    val deliveryFee: Double = 2.50,
    val taxRatePercent: Double = 8.0,
    val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    val id: String,
    val name: String,
    val icon: String = "",
    val itemCount: Int = 0
)

data class ProductItem(
    val id: Int,
    val categoryId: String,
    val name: String,
    val tagline: String = "",
    val description: String = "",
    val basePrice: Double,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val prepTime: String = "",
    val calories: Int? = null,
    val imageUrl: String,
    val badge: String? = null,
    val ingredients: List<String> = emptyList(),
    val sizes: List<ProductSizeOption> = emptyList(),
    val sugarLevels: List<String> = emptyList(),
    val milkOptions: List<ProductMilkOption> = emptyList()
)

data class ProductSizeOption(
    val id: String,
    val label: String,
    val extraPrice: Double = 0.0
)

data class ProductMilkOption(
    val id: String,
    val name: String,
    val extraPrice: Double = 0.0
)

data class ActiveOrder(
    val orderId: String,
    val estimatedWait: String = "20 - 30 minutes",
    val itemCount: Int,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
