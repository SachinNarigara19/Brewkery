package com.clickretina.brewkery

import android.app.Application
import com.clickretina.brewkery.data.remote.NetworkModule
import com.clickretina.brewkery.data.repository.CoffeeRepository
import com.clickretina.brewkery.data.repository.CoffeeRepositoryImpl

class BrewkeryApplication : Application() {

    lateinit var coffeeRepository: CoffeeRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        coffeeRepository = CoffeeRepositoryImpl(
            apiService = NetworkModule.apiService
        )
    }

    companion object {
        lateinit var instance: BrewkeryApplication
            private set
    }
}
