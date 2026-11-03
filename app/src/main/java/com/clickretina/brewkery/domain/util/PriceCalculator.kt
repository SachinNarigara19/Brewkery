package com.clickretina.brewkery.domain.util

import com.clickretina.brewkery.domain.model.CartItem
import java.math.BigDecimal
import java.math.RoundingMode

data class BillBreakdown(
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val taxRatePercent: Double = 8.0,
    val estimatedTax: Double = 0.0,
    val totalPayable: Double = 0.0,
    val itemCount: Int = 0
)

class PriceCalculator(
    val defaultDeliveryFee: Double = 2.50,
    val defaultTaxRatePercent: Double = 8.0
) {
    fun calculateSubtotal(items: List<CartItem>): Double {
        val sum = items.sumOf { it.totalPrice }
        return roundCurrency(sum)
    }

    fun calculateDeliveryFee(items: List<CartItem>, fee: Double = defaultDeliveryFee): Double {
        return if (items.isEmpty()) 0.0 else roundCurrency(fee)
    }

    fun calculateTax(subtotal: Double, taxRatePercent: Double = defaultTaxRatePercent): Double {
        if (subtotal <= 0.0) return 0.0
        val rawTax = subtotal * (taxRatePercent / 100.0)
        return roundCurrency(rawTax)
    }

    fun calculateTotalPayable(subtotal: Double, deliveryFee: Double, tax: Double): Double {
        if (subtotal <= 0.0) return 0.0
        return roundCurrency(subtotal + deliveryFee + tax)
    }

    fun calculateBill(
        items: List<CartItem>,
        deliveryFee: Double = defaultDeliveryFee,
        taxRatePercent: Double = defaultTaxRatePercent
    ): BillBreakdown {
        val subtotal = calculateSubtotal(items)
        val fee = calculateDeliveryFee(items, deliveryFee)
        val tax = calculateTax(subtotal, taxRatePercent)
        val total = calculateTotalPayable(subtotal, fee, tax)
        val count = items.sumOf { it.quantity }
        return BillBreakdown(
            subtotal = subtotal,
            deliveryFee = fee,
            taxRatePercent = taxRatePercent,
            estimatedTax = tax,
            totalPayable = total,
            itemCount = count
        )
    }

    private fun roundCurrency(value: Double): Double {
        return BigDecimal(value.toString())
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    companion object {
        val DEFAULT = PriceCalculator()
    }
}
