package com.clickretina.brewkery.data.mapper

import com.clickretina.brewkery.data.remote.dto.CategoryDto
import com.clickretina.brewkery.data.remote.dto.MilkOptionDto
import com.clickretina.brewkery.data.remote.dto.ProductItemDto
import com.clickretina.brewkery.data.remote.dto.SizeOptionDto
import com.clickretina.brewkery.data.remote.dto.StoreMetaDto
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.StoreMeta

fun StoreMetaDto.toDomain(): StoreMeta {
    return StoreMeta(
        app = app ?: "Brewkery",
        version = version ?: "1.0.0",
        tagline = tagline ?: "Artisanal Coffee & Fresh Oven Bakes",
        currency = currency ?: "USD",
        currencySymbol = currencySymbol ?: "$",
        deliveryFee = deliveryFee ?: 2.50,
        taxRatePercent = taxRatePercent ?: 8.0,
        estimatedDeliveryTime = estimatedDeliveryTime ?: "20 - 30 mins"
    )
}

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon.orEmpty(),
        itemCount = itemCount ?: 0
    )
}

fun SizeOptionDto.toDomain(): ProductSizeOption {
    return ProductSizeOption(
        id = id,
        label = label,
        extraPrice = extraPrice
    )
}

fun MilkOptionDto.toDomain(): ProductMilkOption {
    return ProductMilkOption(
        id = id,
        name = name,
        extraPrice = extraPrice
    )
}

fun ProductItemDto.toDomain(): ProductItem {
    return ProductItem(
        id = id,
        categoryId = categoryId,
        name = name,
        tagline = tagline.orEmpty(),
        description = description.orEmpty(),
        basePrice = basePrice,
        rating = rating ?: 0.0,
        reviewCount = reviewCount ?: 0,
        prepTime = prepTime.orEmpty(),
        calories = calories,
        imageUrl = imageUrl,
        badge = badge,
        ingredients = ingredients,
        sizes = customizations?.sizes?.map { it.toDomain() } ?: emptyList(),
        sugarLevels = customizations?.sugarLevels ?: emptyList(),
        milkOptions = customizations?.milkOptions?.map { it.toDomain() } ?: emptyList()
    )
}
