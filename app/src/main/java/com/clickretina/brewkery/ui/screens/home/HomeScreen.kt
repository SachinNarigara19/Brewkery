package com.clickretina.brewkery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.ui.components.BrewkeryTopBar
import com.clickretina.brewkery.ui.components.CategoryFilterRow
import com.clickretina.brewkery.ui.components.CoffeeCard
import com.clickretina.brewkery.ui.theme.CaramelAccent
import com.clickretina.brewkery.ui.theme.CoffeePrimary
import com.clickretina.brewkery.ui.theme.DarkRoastText
import com.clickretina.brewkery.ui.theme.EspressoDark
import com.clickretina.brewkery.ui.theme.LatteCream
import com.clickretina.brewkery.ui.theme.LightMutedText
import com.clickretina.brewkery.ui.theme.MediumRoastText
import com.clickretina.brewkery.ui.theme.OutlineWarm
import com.clickretina.brewkery.ui.theme.WarmBackground
import com.clickretina.brewkery.ui.theme.WarmSurface
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onCategorySelected: (com.clickretina.brewkery.data.model.CoffeeCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCoffeeClick: (String) -> Unit,
    onQuickAdd: (com.clickretina.brewkery.data.model.CoffeeItem) -> Unit,
    onCartClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            BrewkeryTopBar(
                title = "Brewkery",
                canNavigateBack = false,
                cartItemCount = uiState.cartCount,
                onCartClick = onCartClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = WarmBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Promotional Banner
                item(span = { GridItemSpan(2) }) {
                    PromoBanner()
                }

                // Search Bar
                item(span = { GridItemSpan(2) }) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text(
                                text = "Search coffee, roast, bakery...",
                                color = LightMutedText,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = CaramelAccent
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WarmSurface,
                            unfocusedContainerColor = WarmSurface,
                            focusedBorderColor = CoffeePrimary,
                            unfocusedBorderColor = OutlineWarm
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Category Chips
                item(span = { GridItemSpan(2) }) {
                    CategoryFilterRow(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = onCategorySelected,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Section Header
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Artisanal Selection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkRoastText
                        )
                        Text(
                            text = "${uiState.filteredCoffees.size} items",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightMutedText
                        )
                    }
                }

                // Loading State
                if (uiState.isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CoffeePrimary)
                        }
                    }
                } else if (uiState.filteredCoffees.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No coffee items found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MediumRoastText
                            )
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredCoffees,
                        key = { it.id }
                    ) { coffee ->
                        CoffeeCard(
                            coffee = coffee,
                            onClick = { onCoffeeClick(coffee.id) },
                            onAddClick = {
                                onQuickAdd(coffee)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Added ${coffee.name} to cart")
                                }
                            }
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(EspressoDark, CoffeePrimary, CaramelAccent)
                )
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CaramelAccent)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "SPECIAL PROMO",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Crafted to Perfection",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enjoy 20% off on your first artisanal blend order.",
                    color = LatteCream,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Coffee,
                contentDescription = null,
                tint = LatteCream.copy(alpha = 0.8f),
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

