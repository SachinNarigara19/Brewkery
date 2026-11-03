package com.clickretina.brewkery.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
        fun createRoute(productId: String) = "detail/$productId"
    }
    data object Cart : Screen("cart")
    data object OrderStatus : Screen("order_status")
}
