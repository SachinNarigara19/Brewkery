package com.clickretina.brewkery

import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.util.PriceCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PriceCalculatorTest {

    private lateinit var calculator: PriceCalculator

    private val sampleProduct1 = ProductItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        basePrice = 4.85,
        imageUrl = "https://example.com/1.jpg"
    )

    private val sampleProduct2 = ProductItem(
        id = 3,
        categoryId = "cat_bakery",
        name = "Golden Normandy Croissant",
        basePrice = 3.90,
        imageUrl = "https://example.com/3.jpg"
    )

    @Before
    fun setUp() {
        calculator = PriceCalculator(defaultDeliveryFee = 2.50, defaultTaxRatePercent = 8.0)
    }

    @Test
    fun `empty cart returns all zero amounts`() {
        val bill = calculator.calculateBill(emptyList())

        assertEquals(0.0, bill.subtotal, 0.001)
        assertEquals(0.0, bill.deliveryFee, 0.001)
        assertEquals(0.0, bill.estimatedTax, 0.001)
        assertEquals(0.0, bill.totalPayable, 0.001)
        assertEquals(0, bill.itemCount)
    }

    @Test
    fun `single item calculation with size and milk extras`() {
        // Base 4.85 + Grande (0.65) + Almond (0.50) = 6.00 unit price * 2 qty = 12.00
        val item = CartItem(
            product = sampleProduct1,
            selectedSize = ProductSizeOption("sz_medium", "Grande (12 oz)", 0.65),
            selectedMilk = ProductMilkOption("m_almond", "Roasted Almond Milk", 0.50),
            selectedSugar = "50% Mild",
            quantity = 2
        )

        val bill = calculator.calculateBill(listOf(item))

        assertEquals(12.00, bill.subtotal, 0.001)
        assertEquals(2.50, bill.deliveryFee, 0.001)
        assertEquals(0.96, bill.estimatedTax, 0.001) // 12.00 * 8% = 0.96
        assertEquals(15.46, bill.totalPayable, 0.001) // 12.00 + 2.50 + 0.96
        assertEquals(2, bill.itemCount)
    }

    @Test
    fun `multiple items calculation with correct tax and total`() {
        // Item 1: 4.85 + 0 + 0 = 4.85 * 1 = 4.85
        val item1 = CartItem(
            product = sampleProduct1,
            selectedSize = ProductSizeOption("sz_small", "Tall", 0.0),
            selectedMilk = ProductMilkOption("m_oat", "Oat Milk", 0.0),
            quantity = 1
        )

        // Item 2: 3.90 + 3.20 + 0.50 = 7.60 * 1 = 7.60
        val item2 = CartItem(
            product = sampleProduct2,
            selectedSize = ProductSizeOption("sz_pair", "Pair Box", 3.20),
            selectedMilk = ProductMilkOption("top_butter", "Whipped Butter", 0.50),
            quantity = 1
        )

        val bill = calculator.calculateBill(listOf(item1, item2))

        // Subtotal = 4.85 + 7.60 = 12.45
        assertEquals(12.45, bill.subtotal, 0.001)
        assertEquals(2.50, bill.deliveryFee, 0.001)
        // Tax = 12.45 * 0.08 = 0.996 -> rounds to 1.00
        assertEquals(1.00, bill.estimatedTax, 0.001)
        // Total = 12.45 + 2.50 + 1.00 = 15.95
        assertEquals(15.95, bill.totalPayable, 0.001)
        assertEquals(2, bill.itemCount)
    }
}
