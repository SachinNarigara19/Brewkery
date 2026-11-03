package com.clickretina.brewkery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.ui.theme.CoffeePrimary
import com.clickretina.brewkery.ui.theme.MediumRoastText
import com.clickretina.brewkery.ui.theme.SurfaceSubtle

@Composable
fun CategoryFilterRow(
    selectedCategory: CoffeeCategory,
    onCategorySelected: (CoffeeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CoffeeCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            val background = if (isSelected) CoffeePrimary else SurfaceSubtle
            val textColor = if (isSelected) Color.White else MediumRoastText

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(background)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category.displayName,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

