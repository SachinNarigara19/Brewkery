package com.clickretina.brewkery

import com.clickretina.brewkery.data.remote.RetrofitClient
import com.clickretina.brewkery.data.repository.BrewkeryRepositoryImpl
import com.clickretina.brewkery.domain.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BrewkeryRealApiIntegrationTest {

    private lateinit var repository: BrewkeryRepositoryImpl

    @Before
    fun setup() {
        repository = BrewkeryRepositoryImpl(
            apiService = RetrofitClient.apiService,
            ioDispatcher = Dispatchers.IO
        )
    }

    @Test
    fun realApi_fetchMenu_returnsAll6ItemsWithExactMeta() = runTest {
        val result = repository.getMenu(forceRefresh = true)
        assertTrue(
            "Expected Resource.Success but got $result",
            result is Resource.Success
        )

        val items = (result as Resource.Success).data
        assertEquals("Expected exactly 6 items from data.json", 6, items.size)

        // Verify meta
        val meta = repository.storeMeta.value
        assertEquals("Brewkery", meta.app)
        assertEquals(2.50, meta.deliveryFee, 0.001)
        assertEquals(8.0, meta.taxRatePercent, 0.001)

        // Verify categories
        val categories = repository.categories.value
        assertEquals(3, categories.size)
        assertTrue(categories.any { it.id == "cat_hot_coffee" })
        assertTrue(categories.any { it.id == "cat_cold_brews" })
        assertTrue(categories.any { it.id == "cat_bakery" })

        // Check first item attributes
        val item1 = items.first { it.id == 1 }
        assertEquals("Toasted Caramel Macchiato", item1.name)
        assertEquals(4.85, item1.basePrice, 0.001)
        assertEquals("BESTSELLER", item1.badge)
        assertEquals(3, item1.sizes.size)
        assertEquals(3, item1.milkOptions.size)
        assertEquals(3, item1.sugarLevels.size)
    }

    @Test
    fun realApi_fetchItemDetail_returnsItemWithCustomizations() = runTest {
        for (id in 1..6) {
            val result = repository.getItemDetail(id)
            assertTrue(
                "Item #$id should resolve successfully from api/items/$id.json, got: $result",
                result is Resource.Success
            )
            val item = (result as Resource.Success).data
            assertEquals(id, item.id)
            assertNotNull(item.name)
            assertTrue(item.basePrice > 0)
            assertTrue(item.imageUrl.startsWith("https://"))
        }
    }
}

