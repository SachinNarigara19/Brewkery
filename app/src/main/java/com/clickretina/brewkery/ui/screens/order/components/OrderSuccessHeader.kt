package com.clickretina.brewkery.ui.screens.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.OrangeLightBg
import com.clickretina.brewkery.ui.theme.WarmSurface

@Composable
fun OrderSuccessHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. Center Circular Coffee Icon with Orange Border
        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(WarmSurface)
                .border(width = 3.dp, color = OrangeAccent, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "☕",
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Label "ORDER DISPATCHED"
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(OrangeLightBg)
                .border(1.dp, OrangeAccent.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ORDER DISPATCHED",
                color = OrangeAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Heading "Brewing in Progress!"
        Text(
            text = "Brewing in Progress!",
            color = DarkBrownText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 5. Subtitle "Your ticket was dispatched to our barista."
        Text(
            text = "Your ticket was dispatched to our barista.",
            color = MutedBrownText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

