package com.clickretina.brewkery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.ui.screens.cart.CartScreen
import com.clickretina.brewkery.ui.screens.cart.CartViewModel
import com.clickretina.brewkery.ui.screens.detail.DetailScreen
import com.clickretina.brewkery.ui.screens.home.HomeScreen
import com.clickretina.brewkery.ui.screens.order.OrderStatusScreen

@Composable
fun BrewkeryNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Menu.route,
    repository: BrewkeryRepository = BrewkeryApplication.instance.brewkeryRepository
) {
    // Shared application/graph-level CartViewModel for consistent cart state
    val sharedCartViewModel: CartViewModel = viewModel(
        factory = CartViewModel.Factory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Menu Screen
        composable(Screen.Menu.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.Customizer.createRoute(productId))
                },
                onCustomizeClick = { productId ->
                    navController.navigate(Screen.Customizer.createRoute(productId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onTrackOrderClick = {
                    navController.navigate(Screen.OrderConfirmation.route)
                }
            )
        }

        // 2. Item Customizer Screen
        composable(
            route = Screen.Customizer.route,
            arguments = listOf(
                navArgument(Screen.Customizer.ARG_PRODUCT_ID) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt(Screen.Customizer.ARG_PRODUCT_ID) ?: 1

            DetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        // 3. Cart Screen (using shared CartViewModel)
        composable(Screen.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToOrderStatus = {
                    navController.navigate(Screen.OrderConfirmation.route)
                },
                onExploreMenuClick = {
                    navController.popBackStack(Screen.Menu.route, inclusive = false)
                },
                viewModel = sharedCartViewModel
            )
        }

        // 4. Order Confirmation Screen
        composable(Screen.OrderConfirmation.route) {
            OrderStatusScreen(
                onBackToMenuClick = {
                    navController.popBackStack(Screen.Menu.route, inclusive = false)
                }
            )
        }
    }
}
