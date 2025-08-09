package com.amirmuhsin.listinghelper.ui.s1_home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.repository.ProductLocalRepositoryImpl

class HomeViewModelFactory(
    val appContext: Context
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                ProductLocalRepositoryImpl(
                    AppDatabase.getInstance(appContext).productDao()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}