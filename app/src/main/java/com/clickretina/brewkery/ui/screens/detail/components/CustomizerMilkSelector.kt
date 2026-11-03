package com.clickretina.brewkery.ui.screens.detail.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.OrangeLightBg
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun CustomizerMilkSelector(
    milkOptions: List<ProductMilkOption>,
    selectedMilk: ProductMilkOption?,
    onMilkSelected: (ProductMilkOption) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Milk Options / Spreads"
) {
    if (milkOptions.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = DarkBrownText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            milkOptions.forEach { milk ->
                val isSelected = milk.id == selectedMilk?.id
                val shape = RoundedCornerShape(14.dp)
                val bgColor = if (isSelected) OrangeLightBg.copy(alpha = 0.5f) else WarmSurface
                val borderColor = if (isSelected) OrangeAccent else BorderSubtle
                val borderWidth = if (isSelected) 1.5.dp else 1.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(bgColor)
                        .border(borderWidth, borderColor, shape)
                        .clickable { onMilkSelected(milk) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio Button Visual
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) OrangeAccent else BorderSubtle,
                                    shape = CircleShape
                                )
                                .background(WarmSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(OrangeAccent)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Option Name
                        Text(
                            text = milk.name,
                            color = DarkBrownText,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        // Extra Price
                        val priceText = if (milk.extraPrice > 0.0) {
                            "+$${String.format(Locale.US, "%.2f", milk.extraPrice)}"
                        } else {
                            "+$0.00"
                        }

                        Text(
                            text = priceText,
                            color = if (isSelected) OrangeAccent else MutedBrownText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

