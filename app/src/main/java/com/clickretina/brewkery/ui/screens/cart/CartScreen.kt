package com.clickretina.brewkery.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.ui.components.BrewkeryTopBar
import com.clickretina.brewkery.ui.theme.CaramelAccent
import com.clickretina.brewkery.ui.theme.CoffeePrimary
import com.clickretina.brewkery.ui.theme.DarkRoastText
import com.clickretina.brewkery.ui.theme.LightMutedText
import com.clickretina.brewkery.ui.theme.MediumRoastText
import com.clickretina.brewkery.ui.theme.OutlineWarm
import com.clickretina.brewkery.ui.theme.SuccessGreen
import com.clickretina.brewkery.ui.theme.SurfaceSubtle
import com.clickretina.brewkery.ui.theme.WarmBackground
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun CartScreen(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onQuantityChange: (String, CoffeeSize, Int) -> Unit,
    onRemoveItem: (String, CoffeeSize) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Scaffold(
        topBar = {
            BrewkeryTopBar(
                title = "Order Cart",
                canNavigateBack = true,
                onNavigateBack = onBackClick,
                cartItemCount = uiState.items.sumOf { it.quantity },
                onCartClick = {}
            )
        },
        bottomBar = {
            if (uiState.items.isNotEmpty() && !uiState.isOrderPlaced) {
                Surface(
                    color = WarmSurface,
                    shadowElevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = LightMutedText, fontSize = 14.sp)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", uiState.subtotal)}",
                                color = DarkRoastText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", color = LightMutedText, fontSize = 14.sp)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", uiState.deliveryFee)}",
                                color = DarkRoastText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = OutlineWarm.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", uiState.grandTotal)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = CaramelAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Place Coffee Order",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isOrderPlaced) {
                // Success State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SuccessGreen,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Order Brewed & Confirmed!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkRoastText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your artisanal coffee order has been placed. Our barista is getting your brew ready!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MediumRoastText,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onContinueShopping,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary)
                    ) {
                        Text("Back to Menu", color = Color.White)
                    }
                }
            } else if (uiState.items.isEmpty()) {
                // Empty Cart State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Cart",
                        tint = OutlineWarm,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkRoastText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore our warm handcrafted brews and add your favorites.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightMutedText
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onContinueShopping,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary)
                    ) {
                        Text("Explore Coffee Menu", color = Color.White)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.items,
                        key = { "${it.coffeeItem.id}_${it.selectedSize}_${it.selectedSugar}" }
                    ) { item ->
                        CartItemRow(
                            item = item,
                            onQuantityChange = { q ->
                                onQuantityChange(item.coffeeItem.id, item.selectedSize, q)
                            },
                            onRemove = {
                                onRemoveItem(item.coffeeItem.id, item.selectedSize)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.coffeeItem.imageUrl,
                contentDescription = item.coffeeItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.coffeeItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkRoastText
                )
                Text(
                    text = "${item.selectedSize.label} • ${item.selectedSugar.label.substringBefore(" ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightMutedText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format(Locale.US, "%.2f", item.totalPrice)}",
                    color = CaramelAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = {
                        if (item.quantity > 1) {
                            onQuantityChange(item.quantity - 1)
                        } else {
                            onRemove()
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                        contentDescription = "Decrease",
                        tint = DarkRoastText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = DarkRoastText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
