package com.clickretina.brewkery.data.repository

import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.data.model.CoffeeItem
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel
import kotlinx.coroutines.flow.StateFlow

interface CoffeeRepository {
    val cartItems: StateFlow<List<CartItem>>
    suspend fun getCoffees(category: CoffeeCategory): Result<List<CoffeeItem>>
    suspend fun getCoffeeById(id: String): Result<CoffeeItem>
    fun addToCart(coffeeItem: CoffeeItem, size: CoffeeSize, sugar: SugarLevel, quantity: Int = 1)
    fun updateCartItemQuantity(coffeeId: String, size: CoffeeSize, quantity: Int)
    fun removeFromCart(coffeeId: String, size: CoffeeSize)
    fun clearCart()
}
