package com.clickretina.brewkery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.ui.screens.home.HomeUiState
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.EspressoBlack
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.WarmSurface

@Composable
fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All Items" Chip
        val isAllSelected = selectedCategoryId == HomeUiState.CATEGORY_ALL
        CategoryChip(
            label = "All Items",
            isSelected = isAllSelected,
            onClick = { onCategorySelected(HomeUiState.CATEGORY_ALL) }
        )

        // API Dynamic Categories: ☕ Hot Coffee, 🧊 Cold Brews, 🥐 Artisan Bakery
        categories.forEach { category ->
            val isSelected = category.id == selectedCategoryId
            val displayLabel = if (category.icon.isNotBlank()) {
                "${category.icon} ${category.name}"
            } else {
                category.name
            }

            CategoryChip(
                label = displayLabel,
                isSelected = isSelected,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) EspressoBlack else WarmSurface
    val textColor = if (isSelected) Color.White else MutedBrownText
    val borderColor = if (isSelected) EspressoBlack else BorderSubtle

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

