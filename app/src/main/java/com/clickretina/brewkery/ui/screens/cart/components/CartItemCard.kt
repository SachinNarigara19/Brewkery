package com.clickretina.brewkery.ui.screens.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.ErrorRed
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun CartItemCard(
    item: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productName = item.product?.name ?: item.coffeeItem?.name ?: "Product"
    val imageUrl = item.product?.imageUrl ?: item.coffeeItem?.imageUrl.orEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WarmSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Thumbnail
            AsyncImage(
                model = imageUrl,
                contentDescription = productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BorderSubtle.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                // Product Name
                Text(
                    text = productName,
                    color = DarkBrownText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Customizations (Size • Milk)
                val sizeAndMilk = listOf(
                    item.selectedSize.label.ifBlank { item.legacySize.label },
                    item.selectedMilk.name
                ).filter { it.isNotBlank() && it != "Standard" }.joinToString(" • ")

                if (sizeAndMilk.isNotBlank()) {
                    Text(
                        text = sizeAndMilk,
                        color = MutedBrownText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Serving / Sugar
                val sugar = item.selectedSugar.ifBlank { item.legacySugar.label }
                if (sugar.isNotBlank() && sugar != "Normal") {
                    Text(
                        text = sugar,
                        color = MutedBrownText.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Calculated Item Price
                Text(
                    text = "$${String.format(Locale.US, "%.2f", item.totalPrice)}",
                    color = OrangeAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Stepper Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Minus / Remove Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(WarmSurface)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .clickable {
                            if (item.quantity > 1) {
                                onDecreaseQuantity()
                            } else {
                                onRemoveClick()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                        contentDescription = if (item.quantity > 1) "Decrease" else "Remove",
                        tint = if (item.quantity > 1) DarkBrownText else ErrorRed,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Quantity Text
                Text(
                    text = item.quantity.toString(),
                    color = DarkBrownText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Plus Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(OrangeAccent)
                        .clickable(onClick = onIncreaseQuantity),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
