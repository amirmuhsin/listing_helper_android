package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import com.amirmuhsin.listinghelper.repository.PhotoPairLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductRemoteRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for repository dependencies
 */
val repositoryModule = module {

    // Local Repositories
    single<ProductLocalRepository> {
        ProductLocalRepositoryImpl(get())
    }

    single<PhotoPairLocalRepository> {
        PhotoPairLocalRepositoryImpl(get())
    }

    // Remote Repositories
    single<ProductRemoteRepository> {
        ProductRemoteRepositoryImpl(
            context = androidContext(),
            productService = get(),
            imageService = get()
        )
    }
}
