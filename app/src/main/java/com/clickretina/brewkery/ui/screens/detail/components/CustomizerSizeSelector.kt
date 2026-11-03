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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.OrangeLightBg
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun CustomizerSizeSelector(
    sizes: List<ProductSizeOption>,
    selectedSize: ProductSizeOption?,
    onSizeSelected: (ProductSizeOption) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Size Selection"
) {
    if (sizes.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = DarkBrownText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sizes.forEach { size ->
                val isSelected = size.id == selectedSize?.id
                val shape = RoundedCornerShape(14.dp)
                val bgColor = if (isSelected) OrangeLightBg else WarmSurface
                val borderColor = if (isSelected) OrangeAccent else BorderSubtle
                val borderWidth = if (isSelected) 1.5.dp else 1.dp

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(bgColor)
                        .border(borderWidth, borderColor, shape)
                        .clickable { onSizeSelected(size) }
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = size.label,
                            color = DarkBrownText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val priceText = if (size.extraPrice > 0.0) {
                            "+$${String.format(Locale.US, "%.2f", size.extraPrice)}"
                        } else {
                            "+$0.00"
                        }

                        Text(
                            text = priceText,
                            color = if (isSelected) OrangeAccent else MutedBrownText,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

