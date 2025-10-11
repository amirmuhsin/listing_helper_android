package com.amirmuhsin.listinghelper.di

import android.content.Context
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.data.db.dao.PhotoPairDao
import com.amirmuhsin.listinghelper.data.db.dao.ProductDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for database-related dependencies
 */
val databaseModule = module {

    // AppDatabase singleton
    single {
        AppDatabase.getInstance(androidContext())
    }

    // DAOs
    single<ProductDao> {
        get<AppDatabase>().productDao()
    }

    single<PhotoPairDao> {
        get<AppDatabase>().photoPairDao()
    }
}
