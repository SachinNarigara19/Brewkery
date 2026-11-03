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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.util.BillBreakdown
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmSurface
import java.util.Locale

@Composable
fun CartCheckoutSection(
    bill: BillBreakdown,
    onPlaceOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarmSurface,
        shadowElevation = 14.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BorderSubtle.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Bill Breakdown Rows
            BillRow(
                label = "Subtotal",
                value = "$${String.format(Locale.US, "%.2f", bill.subtotal)}"
            )

            Spacer(modifier = Modifier.height(6.dp))

            BillRow(
                label = "Delivery Fee",
                value = "$${String.format(Locale.US, "%.2f", bill.deliveryFee)}"
            )

            Spacer(modifier = Modifier.height(6.dp))

            val taxPercentStr = String.format(Locale.US, "%.0f", bill.taxRatePercent)
            BillRow(
                label = "Estimated Tax ($taxPercentStr%)",
                value = "$${String.format(Locale.US, "%.2f", bill.estimatedTax)}"
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Total Payable Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Payable",
                    color = DarkBrownText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "$${String.format(Locale.US, "%.2f", bill.totalPayable)}",
                    color = OrangeAccent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Place Order Button with Lock Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(OrangeAccent)
                    .clickable(onClick = onPlaceOrderClick),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Secure Checkout",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Place Order Now",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = " • $${String.format(Locale.US, "%.2f", bill.totalPayable)}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun BillRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MutedBrownText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = DarkBrownText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

