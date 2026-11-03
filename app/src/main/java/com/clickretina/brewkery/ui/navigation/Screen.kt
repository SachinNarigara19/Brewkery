package com.clickretina.brewkery.ui.navigation

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Customizer : Screen("customizer/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: Int): String = "customizer/$productId"
        fun createRoute(productId: String): String = "customizer/$productId"
    }
    data object Cart : Screen("cart")
    data object OrderConfirmation : Screen("orderConfirmation")

    companion object {
        val Home get() = Menu
        val Detail get() = Customizer
        val OrderStatus get() = OrderConfirmation
    }
}
