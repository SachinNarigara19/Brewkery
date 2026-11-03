package com.clickretina.brewkery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.clickretina.brewkery.ui.screens.cart.CartScreen
import com.clickretina.brewkery.ui.screens.detail.DetailScreen
import com.clickretina.brewkery.ui.screens.home.HomeScreen

@Composable
fun BrewkeryNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.Detail.createRoute(productId))
                },
                onCustomizeClick = { productId ->
                    navController.navigate(Screen.Detail.createRoute(productId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onTrackOrderClick = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productIdStr = backStackEntry.arguments?.getString("productId").orEmpty()
            val productId = productIdStr.toIntOrNull() ?: 1

            DetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToOrderStatus = {
                    navController.navigate(Screen.OrderStatus.route)
                },
                onExploreMenuClick = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(Screen.OrderStatus.route) {
            CartScreen(
                onBackClick = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onNavigateToOrderStatus = {},
                onExploreMenuClick = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
    }
}

