package com.amirmuhsin.listinghelper.util

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat

inline fun <reified T: Parcelable> Bundle.parcelable(key: String): T? =
    BundleCompat.getParcelable(this, key, T::class.java)

inline fun <reified T: Parcelable> Intent.parcelable(key: String): T? =
    IntentCompat.getParcelableExtra(this, key, T::class.java)

inline fun <reified T: Parcelable> Bundle.parcelableList(key: String): List<T>? =
    BundleCompat.getParcelableArrayList(this, key, T::class.java)

inline fun <reified T: Parcelable> Intent.parcelableList(key: String): List<T>? =
    IntentCompat.getParcelableArrayListExtra(this, key, T::class.java)