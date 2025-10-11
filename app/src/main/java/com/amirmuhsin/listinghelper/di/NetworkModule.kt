package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.data.networking.PhotoRoomNetworkModule
import com.amirmuhsin.listinghelper.data.networking.ProductNetworkModule
import com.amirmuhsin.listinghelper.data.networking.api.ImageService
import com.amirmuhsin.listinghelper.data.networking.api.PhotoRoomService
import com.amirmuhsin.listinghelper.data.networking.api.ProductService
import org.koin.dsl.module

/**
 * Koin module for network-related dependencies
 */
val networkModule = module {

    // Product API Services
    single<ProductService> {
        ProductNetworkModule.productService
    }

    single<ImageService> {
        ProductNetworkModule.imageService
    }

    // PhotoRoom API Service - DEPRECATED: No longer in use
    @Suppress("DEPRECATION")
    single<PhotoRoomService> {
        PhotoRoomNetworkModule.photoRoomService
    }
}
