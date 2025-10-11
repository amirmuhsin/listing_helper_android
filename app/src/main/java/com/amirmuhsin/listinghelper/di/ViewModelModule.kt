package com.amirmuhsin.listinghelper.di

import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.FullScreenViewerViewModel
import com.amirmuhsin.listinghelper.ui.common.main.MainViewModel
import com.amirmuhsin.listinghelper.ui.empty.EmptyViewModel
import com.amirmuhsin.listinghelper.ui.s1_home.HomeViewModel
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.ProductDetailViewModel
import com.amirmuhsin.listinghelper.ui.s3_photo_capture.PhotoCaptureViewModel
import com.amirmuhsin.listinghelper.ui.s4_bg_clean.BgCleanerViewModel
import com.amirmuhsin.listinghelper.ui.s5_review_upload.ReviewUploadViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for ViewModel dependencies
 */
val viewModelModule = module {

    // Main ViewModels
    viewModel {
        MainViewModel()
    }

    viewModel {
        HomeViewModel(
            productLocalRepository = get()
        )
    }

    viewModel {
        ProductDetailViewModel(
            productLocalRepository = get(),
            photoPairLocalRepository = get(),
            productRemoteRepository = get()
        )
    }

    viewModel {
        PhotoCaptureViewModel()
    }

    viewModel {
        BgCleanerViewModel(
            service = get(),
            appContext = androidContext()
        )
    }

    viewModel {
        ReviewUploadViewModel(
            productLocalRepository = get(),
            photoPairLocalRepository = get(),
            productRemoteRepository = get()
        )
    }

    viewModel { (productId: Long, startPhotoPairId: String) ->
        FullScreenViewerViewModel(
            appContext = androidContext(),
            productId = productId,
            startPhotoPairId = startPhotoPairId,
            productLocalRepository = get(),
            photoPairLocalRepository = get()
        )
    }

    viewModel {
        EmptyViewModel()
    }
}
