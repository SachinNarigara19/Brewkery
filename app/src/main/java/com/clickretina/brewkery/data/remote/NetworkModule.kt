package com.clickretina.brewkery.data.remote

object NetworkModule {
    val apiService: BrewkeryApiService get() = RetrofitClient.apiService
    val okHttpClient get() = RetrofitClient.okHttpClient
}
