package com.clickretina.brewkery.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{coffeeId}") {
        fun createRoute(coffeeId: String) = "detail/$coffeeId"
    }
    data object Cart : Screen("cart")
}

