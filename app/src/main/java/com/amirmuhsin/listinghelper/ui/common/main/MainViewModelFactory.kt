package com.amirmuhsin.listinghelper.ui.common.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.repository.PhotoPairLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductLocalRepositoryImpl

class MainViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        val productRepo = ProductLocalRepositoryImpl(db.productDao())
        val photoRepo = PhotoPairLocalRepositoryImpl(db.photoPairDao())
        return MainViewModel(productRepo, photoRepo) as T
    }
}
