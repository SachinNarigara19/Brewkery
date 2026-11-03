package com.clickretina.brewkery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.ui.screens.cart.CartScreen
import com.clickretina.brewkery.ui.screens.cart.CartViewModel
import com.clickretina.brewkery.ui.screens.detail.DetailScreen
import com.clickretina.brewkery.ui.screens.detail.DetailViewModel
import com.clickretina.brewkery.ui.screens.home.HomeScreen
import com.clickretina.brewkery.ui.screens.home.HomeViewModel

@Composable
fun BrewkeryNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val repository = BrewkeryApplication.instance.coffeeRepository

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            val uiState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
                onCategorySelected = homeViewModel::onCategorySelected,
                onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                onCoffeeClick = { coffeeId ->
                    navController.navigate(Screen.Detail.createRoute(coffeeId))
                },
                onQuickAdd = homeViewModel::quickAddToCart,
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("coffeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val coffeeId = backStackEntry.arguments?.getString("coffeeId").orEmpty()
            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.Factory(coffeeId, repository)
            )
            val uiState by detailViewModel.uiState.collectAsState()

            DetailScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onSizeSelected = detailViewModel::onSizeSelected,
                onSugarSelected = detailViewModel::onSugarSelected,
                onIncreaseQuantity = detailViewModel::increaseQuantity,
                onDecreaseQuantity = detailViewModel::decreaseQuantity,
                onAddToCart = detailViewModel::addToCart
            )
        }

        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModel.Factory(repository)
            )
            val uiState by cartViewModel.uiState.collectAsState()

            CartScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onQuantityChange = cartViewModel::updateQuantity,
                onRemoveItem = cartViewModel::removeItem,
                onCheckout = cartViewModel::checkout,
                onContinueShopping = {
                    cartViewModel.resetOrderState()
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
    }
}
