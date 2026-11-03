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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.OrangeLightBg
import com.clickretina.brewkery.ui.theme.WarmSurface

@Composable
fun CustomizerSugarSelector(
    sugarLevels: List<String>,
    selectedSugar: String,
    onSugarSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Sugar Levels / Serving"
) {
    if (sugarLevels.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = DarkBrownText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Check if options are short (e.g. 0% Unsweetened) or longer phrases
        val hasLongLabels = sugarLevels.any { it.length > 18 }

        if (hasLongLabels) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sugarLevels.forEach { level ->
                    SugarOptionItem(
                        label = level,
                        isSelected = level == selectedSugar,
                        onClick = { onSugarSelected(level) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sugarLevels.forEach { level ->
                    SugarOptionItem(
                        label = level,
                        isSelected = level == selectedSugar,
                        onClick = { onSugarSelected(level) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SugarOptionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (isSelected) OrangeLightBg else WarmSurface
    val borderColor = if (isSelected) OrangeAccent else BorderSubtle
    val borderWidth = if (isSelected) 1.5.dp else 1.dp
    val textColor = if (isSelected) OrangeAccent else DarkBrownText

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(borderWidth, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
