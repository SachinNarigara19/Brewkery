package com.clickretina.brewkery.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.ui.screens.cart.components.CartCheckoutSection
import com.clickretina.brewkery.ui.screens.cart.components.CartItemCard
import com.clickretina.brewkery.ui.screens.cart.components.CartTopBar
import com.clickretina.brewkery.ui.screens.cart.components.EmptyCartView
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.ErrorRed
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmBackground
import com.clickretina.brewkery.ui.theme.WarmSurface

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onNavigateToOrderStatus: () -> Unit,
    onExploreMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(
        factory = CartViewModel.Factory(BrewkeryApplication.instance.brewkeryRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.placedOrder) {
        if (uiState.placedOrder != null) {
            onNavigateToOrderStatus()
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear Cart?",
                    color = DarkBrownText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove all handcrafted items from your cart?",
                    color = MutedBrownText,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearCart()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = DarkBrownText)
                }
            },
            containerColor = WarmSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    CartScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onClearCartClick = { showClearDialog = true },
        onExploreMenuClick = onExploreMenuClick,
        onIncreaseQuantity = { cartItemId, currentQty ->
            viewModel.updateQuantity(cartItemId, currentQty + 1)
        },
        onDecreaseQuantity = { cartItemId, currentQty ->
            viewModel.updateQuantity(cartItemId, currentQty - 1)
        },
        onRemoveItem = viewModel::removeItem,
        onPlaceOrderClick = {
            viewModel.placeOrder()
        },
        modifier = modifier
    )
}

@Composable
fun CartScreenContent(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onClearCartClick: () -> Unit,
    onExploreMenuClick: () -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onPlaceOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = WarmBackground,
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                CartCheckoutSection(
                    bill = uiState.bill,
                    onPlaceOrderClick = onPlaceOrderClick
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Header (Back Arrow, Centered "YOUR CART", "Clear Cart" Action)
            CartTopBar(
                hasItems = uiState.items.isNotEmpty(),
                onBackClick = onBackClick,
                onClearCartClick = onClearCartClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Content: Empty State or Cart Items List
            if (uiState.items.isEmpty()) {
                // 3. Empty Cart State
                EmptyCartView(
                    onExploreMenuClick = onExploreMenuClick,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // 2. Cart Items List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = uiState.items,
                        key = { it.internalId }
                    ) { item ->
                        CartItemCard(
                            item = item,
                            onIncreaseQuantity = {
                                onIncreaseQuantity(item.internalId, item.quantity)
                            },
                            onDecreaseQuantity = {
                                onDecreaseQuantity(item.internalId, item.quantity)
                            },
                            onRemoveClick = {
                                onRemoveItem(item.internalId)
                            }
                        )
                    }
                }
            }
        }
    }
}
