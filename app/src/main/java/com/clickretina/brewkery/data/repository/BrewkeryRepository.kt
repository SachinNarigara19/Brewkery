package com.clickretina.brewkery.data.repository

import com.clickretina.brewkery.domain.model.ActiveOrder
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.ProductItem
import com.clickretina.brewkery.domain.model.ProductMilkOption
import com.clickretina.brewkery.domain.model.ProductSizeOption
import com.clickretina.brewkery.domain.model.Resource
import com.clickretina.brewkery.domain.model.StoreMeta
import kotlinx.coroutines.flow.StateFlow

interface BrewkeryRepository {
    val menuState: StateFlow<Resource<List<ProductItem>>>
    val storeMeta: StateFlow<StoreMeta>
    val categories: StateFlow<List<Category>>
    val cartItems: StateFlow<List<CartItem>>
    val activeOrder: StateFlow<ActiveOrder?>

    suspend fun getMenu(forceRefresh: Boolean = false): Resource<List<ProductItem>>
    suspend fun getItemDetail(id: Int): Resource<ProductItem>

    fun addToCart(
        product: ProductItem,
        size: ProductSizeOption,
        milk: ProductMilkOption,
        sugar: String,
        quantity: Int = 1
    )
    fun updateCartQuantity(cartItemId: String, quantity: Int)
    fun removeFromCart(cartItemId: String)
    fun clearCart()
    fun placeOrder(): ActiveOrder?
}
