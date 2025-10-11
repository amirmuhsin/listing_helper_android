package com.amirmuhsin.listinghelper

import android.app.Application
import com.amirmuhsin.listinghelper.di.databaseModule
import com.amirmuhsin.listinghelper.di.networkModule
import com.amirmuhsin.listinghelper.di.repositoryModule
import com.amirmuhsin.listinghelper.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ListingHelperApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin for Dependency Injection
        startKoin {
            // Log Koin into Android logger
            androidLogger(Level.ERROR)
            // Reference Android context
            androidContext(this@ListingHelperApp)
            // Load modules
            modules(
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }

}