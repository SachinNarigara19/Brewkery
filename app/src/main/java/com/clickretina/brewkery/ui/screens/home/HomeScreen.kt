package com.clickretina.brewkery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.ui.components.BrewkeryHeader
import com.clickretina.brewkery.ui.components.BrewkerySearchBar
import com.clickretina.brewkery.ui.components.CategoryChipsRow
import com.clickretina.brewkery.ui.components.DeliveryInfoCard
import com.clickretina.brewkery.ui.components.FloatingCartBar
import com.clickretina.brewkery.ui.components.ProductListItemCard
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmBackground
import com.clickretina.brewkery.ui.theme.WarmSurface

@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    onCustomizeClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(BrewkeryApplication.instance.brewkeryRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onProductClick = onProductClick,
        onCustomizeClick = onCustomizeClick,
        onCartClick = onCartClick,
        onTrackOrderClick = onTrackOrderClick,
        onRetryClick = viewModel::onRetry,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCustomizeClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrderClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = WarmBackground,
        bottomBar = {
            FloatingCartBar(
                cartCount = uiState.cartCount,
                subtotal = uiState.cartSubtotal,
                onCheckoutClick = onCartClick,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Section (Header, Delivery Card, Search Bar, Categories)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 1. Top Header with BK logo, store title and cart button
                BrewkeryHeader(
                    cartCount = uiState.cartCount,
                    onCartClick = onCartClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Delivery / Active Order Status Card
                DeliveryInfoCard(
                    storeMeta = uiState.storeMeta,
                    activeOrder = uiState.activeOrder,
                    onCardClick = {
                        if (uiState.activeOrder != null) {
                            onTrackOrderClick()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Search Bar
                BrewkerySearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = onSearchQueryChanged
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Horizontal Category Filter Chips
                CategoryChipsRow(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategorySelected = onCategorySelected
                )
            }

            // 5. Product Listing (LazyColumn)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading && uiState.products.isEmpty() -> {
                        // Loading State
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = OrangeAccent,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Brewing menu...",
                                    color = MutedBrownText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    uiState.errorMessage != null && uiState.products.isEmpty() -> {
                        // Error State
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(BorderSubtle.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "⚠️", fontSize = 28.sp)
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Couldn't load menu",
                                    color = DarkBrownText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.errorMessage,
                                    color = MutedBrownText,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onRetryClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    uiState.filteredProducts.isEmpty() -> {
                        // Empty Search / Filter State
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔍", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No items found",
                                    color = DarkBrownText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try searching for a different roast, cold brew, or bakery item.",
                                    color = MutedBrownText,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        // Populated Product Items List
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredProducts,
                                key = { it.id }
                            ) { product ->
                                ProductListItemCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onCustomizeClick = { onCustomizeClick(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
