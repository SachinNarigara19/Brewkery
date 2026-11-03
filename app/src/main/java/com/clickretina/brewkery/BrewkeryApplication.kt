package com.clickretina.brewkery

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.clickretina.brewkery.data.remote.RetrofitClient
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.data.repository.BrewkeryRepositoryImpl
import com.clickretina.brewkery.data.repository.CoffeeRepository
import com.clickretina.brewkery.data.repository.CoffeeRepositoryImpl

class BrewkeryApplication : Application(), ImageLoaderFactory {

    lateinit var brewkeryRepository: BrewkeryRepository
        private set

    lateinit var coffeeRepository: CoffeeRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        brewkeryRepository = BrewkeryRepositoryImpl(
            apiService = RetrofitClient.apiService
        )
        coffeeRepository = CoffeeRepositoryImpl(
            apiService = RetrofitClient.apiService
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(RetrofitClient.okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }

    companion object {
        lateinit var instance: BrewkeryApplication
            private set
    }
}
