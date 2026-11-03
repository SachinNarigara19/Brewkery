package com.clickretina.brewkery

import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.util.PriceCalculator
import com.clickretina.brewkery.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BrewkeryNavigationTest {

    @Test
    fun `verify all required navigation destination routes`() {
        assertEquals("menu", Screen.Menu.route)
        assertEquals("customizer/{productId}", Screen.Customizer.route)
        assertEquals("customizer/1", Screen.Customizer.createRoute(1))
        assertEquals("customizer/5", Screen.Customizer.createRoute(5))
        assertEquals("cart", Screen.Cart.route)
        assertEquals("orderConfirmation", Screen.OrderConfirmation.route)
    }

    @Test
    fun `verify backward compatibility route aliases`() {
        assertEquals("menu", Screen.Home.route)
        assertEquals("customizer/2", Screen.Detail.createRoute(2))
        assertEquals("orderConfirmation", Screen.OrderStatus.route)
    }

    @Test
    fun `verify cart maintains subtotal across navigation data simulation`() {
        val calculator = PriceCalculator.DEFAULT
        val item1 = CartItem(
            product = ProductItem(
                id = 1,
                categoryId = "cat_hot_coffee",
                name = "Toasted Caramel Macchiato",
                basePrice = 4.85,
                imageUrl = "https://example.com/1.jpg"
            ),
            selectedSize = ProductSizeOption("sz_medium", "Grande (12 oz)", 0.65),
            selectedMilk = ProductMilkOption("m_almond", "Roasted Almond Milk", 0.50),
            quantity = 2
        )

        // Subtotal = (4.85 + 0.65 + 0.50) * 2 = 12.00
        val bill = calculator.calculateBill(listOf(item1))
        assertEquals(12.00, bill.subtotal, 0.001)
        assertEquals(2.50, bill.deliveryFee, 0.001)
        assertEquals(0.96, bill.estimatedTax, 0.001)
        assertEquals(15.46, bill.totalPayable, 0.001)
        assertEquals(2, bill.itemCount)
    }
}
