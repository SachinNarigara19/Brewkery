package com.clickretina.brewkery

import com.clickretina.brewkery.data.model.CoffeeCategory
import com.clickretina.brewkery.data.remote.RetrofitClient
import com.clickretina.brewkery.data.repository.CoffeeRepositoryImpl
import com.clickretina.brewkery.domain.model.CoffeeSize
import com.clickretina.brewkery.domain.model.SugarLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoffeeRepositoryTest {

    private lateinit var repository: CoffeeRepositoryImpl

    @Before
    fun setup() {
        repository = CoffeeRepositoryImpl(
            apiService = RetrofitClient.apiService,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun getCoffees_returnsFallbackCatalog_whenRemoteFails() = runTest {
        val result = repository.getCoffees(CoffeeCategory.ALL)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun getCoffees_filtersByCategoryCorrectly() = runTest {
        val result = repository.getCoffees(CoffeeCategory.ESPRESSO)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertTrue(list.all { it.category == CoffeeCategory.ESPRESSO })
    }

    @Test
    fun addToCart_updatesCartItemsStateFlow() = runTest {
        val coffee = repository.getCoffees(CoffeeCategory.ALL).getOrThrow().first()
        repository.addToCart(
            coffeeItem = coffee,
            size = CoffeeSize.REGULAR,
            sugar = SugarLevel.NORMAL,
            quantity = 2
        )

        val items = repository.cartItems.value
        assertEquals(1, items.size)
        assertEquals(2, items.first().quantity)
        assertEquals(coffee.id, items.first().coffeeItem?.id)
    }
}
