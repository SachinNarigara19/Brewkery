package com.clickretina.brewkery.ui.screens.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.ui.screens.order.components.OrderSuccessHeader
import com.clickretina.brewkery.ui.screens.order.components.OrderStatusBottomBar
import com.clickretina.brewkery.ui.screens.order.components.OrderTicketCard
import com.clickretina.brewkery.ui.theme.WarmBackground

@Composable
fun OrderStatusScreen(
    onBackToMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderStatusViewModel = viewModel(
        factory = OrderStatusViewModel.Factory(BrewkeryApplication.instance.brewkeryRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OrderStatusScreenContent(
        uiState = uiState,
        onBackToMenuClick = onBackToMenuClick,
        modifier = modifier
    )
}

@Composable
fun OrderStatusScreenContent(
    uiState: OrderStatusUiState,
    onBackToMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = WarmBackground,
        bottomBar = {
            OrderStatusBottomBar(onBackToMenuClick = onBackToMenuClick)
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Success Graphic (Circular Coffee Icon, "ORDER DISPATCHED", Headline, Subtitle)
            OrderSuccessHeader()

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Order Ticket Card (Ticket ID, Preparing Badge, Estimated Wait, Items, Status Message)
            OrderTicketCard(
                orderId = uiState.displayOrderId,
                estimatedWait = uiState.displayWaitTime,
                itemCount = uiState.displayItemCount,
                totalAmount = uiState.displayTotalAmount,
                statusText = uiState.displayStatusText,
                itemsSummary = uiState.displayItemsSummary
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

