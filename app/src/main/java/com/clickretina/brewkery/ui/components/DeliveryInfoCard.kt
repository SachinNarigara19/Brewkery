package com.clickretina.brewkery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.StoreMeta
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.DeliveryCardBg
import com.clickretina.brewkery.ui.theme.DeliveryCardBgEnd
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.SuccessGreen
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun DeliveryInfoCard(
    storeMeta: StoreMeta,
    activeOrder: ActiveOrder?,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOrderActive = activeOrder != null
    val tagText = if (isOrderActive) "ACTIVE ORDER" else "STORE INFO"
    val tagColor = if (isOrderActive) SuccessGreen else OrangeAccent
    val mainText = if (isOrderActive) "Active Order ${activeOrder?.orderId}" else "Delivery in ${storeMeta.estimatedDeliveryTime}"
    val subText = if (isOrderActive) "Preparing (Arriving in ${activeOrder?.estimatedWait})" else "$${String.format(Locale.US, "%.2f", storeMeta.deliveryFee)} flat fee"
    val actionText = if (isOrderActive) "Track" else "Open"
    val iconEmoji = if (isOrderActive) "☕" else "🛵"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(DeliveryCardBg, DeliveryCardBgEnd)
                )
            )
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            .clickable(onClick = onCardClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in white rounded box
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tagText,
                        color = tagColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(tagColor)
                    )
                }

                Text(
                    text = mainText,
                    color = DarkBrownText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subText,
                    color = MutedBrownText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Action Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isOrderActive) SuccessGreen else WarmSurface)
                    .border(1.dp, if (isOrderActive) SuccessGreen else BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionText,
                    color = if (isOrderActive) Color.White else DarkBrownText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

