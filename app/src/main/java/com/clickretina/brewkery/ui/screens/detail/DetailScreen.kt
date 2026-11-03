package com.clickretina.brewkery.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.clickretina.brewkery.BrewkeryApplication
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.ui.screens.detail.components.CustomizerMilkSelector
import com.clickretina.brewkery.ui.screens.detail.components.CustomizerQuantitySelector
import com.clickretina.brewkery.ui.screens.detail.components.CustomizerSizeSelector
import com.clickretina.brewkery.ui.screens.detail.components.CustomizerSugarSelector
import com.clickretina.brewkery.ui.screens.detail.components.CustomizerTopBar
import com.clickretina.brewkery.ui.screens.detail.components.StickyBottomAddToCartBar
import com.clickretina.brewkery.ui.theme.BadgeBg
import com.clickretina.brewkery.ui.theme.BadgeText
import com.clickretina.brewkery.ui.theme.BorderSubtle
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.IngredientChipBg
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmBackground
import com.clickretina.brewkery.ui.theme.WarmSurface
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun DetailScreen(
    productId: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(
        key = "customizer_$productId",
        factory = DetailViewModel.Factory(
            productId = productId,
            repository = BrewkeryApplication.instance.brewkeryRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.isAddedToCart) {
        if (uiState.isAddedToCart) {
            val itemName = uiState.product?.name ?: "Item"
            val result = snackbarHostState.showSnackbar(
                message = "$itemName added to cart!",
                actionLabel = "View Cart",
                duration = SnackbarDuration.Short
            )
            viewModel.resetAddedToCart()
            if (result == SnackbarResult.ActionPerformed) {
                onCartClick()
            }
        }
    }

    DetailScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onFavoriteToggle = viewModel::toggleFavorite,
        onSizeSelected = viewModel::onSizeSelected,
        onMilkSelected = viewModel::onMilkSelected,
        onSugarSelected = viewModel::onSugarSelected,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onDecreaseQuantity = viewModel::decreaseQuantity,
        onAddToCartClick = {
            viewModel.addToCart()
        },
        onRetryClick = viewModel::loadProductDetail,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun DetailScreenContent(
    uiState: DetailUiState,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onSizeSelected: (ProductSizeOption) -> Unit,
    onMilkSelected: (ProductMilkOption) -> Unit,
    onSugarSelected: (String) -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onAddToCartClick: () -> Unit,
    onRetryClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = WarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.product != null) {
                StickyBottomAddToCartBar(
                    totalPrice = uiState.totalPrice,
                    onAddToCartClick = onAddToCartClick
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // 1. Top Toolbar (Back Button, Centered "ITEM CUSTOMIZER", Heart Favorite Button)
            CustomizerTopBar(
                isFavorite = uiState.isFavorite,
                onBackClick = onBackClick,
                onFavoriteToggle = onFavoriteToggle,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Content Area (Loading, Error, or Populated Customizer)
            when {
                uiState.isLoading && uiState.product == null -> {
                    // Loading State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = OrangeAccent,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading item details...",
                                color = MutedBrownText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                uiState.errorMessage != null && uiState.product == null -> {
                    // Error State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(BorderSubtle.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚠️", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Couldn't load item details",
                                color = DarkBrownText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.errorMessage,
                                color = MutedBrownText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onRetryClick,
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                uiState.product != null -> {
                    val product = uiState.product
                    val isBakery = product.categoryId == "cat_bakery"

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        // 2. Large Product Image with Rounded Corners & Badge Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(WarmSurface)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(22.dp))
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )

                            // Badge Overlay (e.g. BESTSELLER, STAFF PICK, FRESHLY BAKED)
                            if (!product.badge.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BadgeBg)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = product.badge.uppercase(),
                                        color = BadgeText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Product Information (Title, Orange Price, Description, Ingredients)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = product.name,
                                color = DarkBrownText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "$${String.format(Locale.US, "%.2f", product.basePrice)}",
                                color = OrangeAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Rating & Reviews row
                        if (product.rating > 0.0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⭐ ${String.format(Locale.US, "%.1f", product.rating)}",
                                    color = DarkBrownText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (product.reviewCount > 0) {
                                    Text(
                                        text = " (${product.reviewCount} reviews)",
                                        color = MutedBrownText,
                                        fontSize = 12.sp
                                    )
                                }
                                if (product.prepTime.isNotBlank()) {
                                    Text(
                                        text = " • ⏱ ${product.prepTime}",
                                        color = MutedBrownText,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Product Description
                        if (product.description.isNotBlank()) {
                            Text(
                                text = product.description,
                                color = MutedBrownText,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Key Ingredient Chips
                        if (product.ingredients.isNotEmpty()) {
                            Text(
                                text = "Key Ingredients",
                                color = DarkBrownText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                product.ingredients.forEach { ingredient ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(IngredientChipBg)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = ingredient,
                                            color = DarkBrownText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Size Selection
                        CustomizerSizeSelector(
                            sizes = product.sizes,
                            selectedSize = uiState.selectedSize,
                            onSizeSelected = onSizeSelected,
                            title = if (isBakery) "Select Serving / Pack" else "Size Selection"
                        )

                        // 5. Milk Options / Spreads
                        if (product.milkOptions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            CustomizerMilkSelector(
                                milkOptions = product.milkOptions,
                                selectedMilk = uiState.selectedMilk,
                                onMilkSelected = onMilkSelected,
                                title = if (isBakery) "Artisan Spreads & Add-ons" else "Milk Options / Spreads"
                            )
                        }

                        // 6. Sugar Levels / Serving
                        if (product.sugarLevels.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            CustomizerSugarSelector(
                                sugarLevels = product.sugarLevels,
                                selectedSugar = uiState.selectedSugar,
                                onSugarSelected = onSugarSelected,
                                title = if (isBakery) "Serving Temperature" else "Sugar Levels / Serving"
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 7. Quantity Selector
                        CustomizerQuantitySelector(
                            quantity = uiState.quantity,
                            onIncrease = onIncreaseQuantity,
                            onDecrease = onDecreaseQuantity
                        )

                        // Bottom Spacer to prevent content being covered by sticky bottom bar
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
