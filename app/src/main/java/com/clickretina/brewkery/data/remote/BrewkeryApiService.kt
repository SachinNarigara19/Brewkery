package com.clickretina.brewkery.data.remote

import com.clickretina.brewkery.data.model.CoffeeItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BrewkeryApiService {

    @GET("coffees")
    suspend fun getCoffees(
        @Query("category") category: String? = null
    ): List<CoffeeItem>

    @GET("coffees/{id}")
    suspend fun getCoffeeById(
        @Path("id") id: String
    ): CoffeeItem
}
