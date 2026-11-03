package com.clickretina.brewkery.data.remote

import com.clickretina.brewkery.data.remote.dto.MenuResponseDto
import com.clickretina.brewkery.data.remote.dto.ProductItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApiService {

    @GET("data.json")
    suspend fun getMenu(): Response<MenuResponseDto>

    @GET("api/items/{id}.json")
    suspend fun getItemById(
        @Path("id") id: Int
    ): Response<ProductItemDto>
}
