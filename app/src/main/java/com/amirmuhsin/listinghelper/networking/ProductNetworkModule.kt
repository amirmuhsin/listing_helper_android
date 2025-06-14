package com.amirmuhsin.listinghelper.networking

import com.amirmuhsin.listinghelper.networking.api.ImageService
import com.amirmuhsin.listinghelper.networking.api.ProductService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ProductNetworkModule {

    private const val BASE_URL = "http://87.106.214.160:5883/api/eazybusiness/"

    private const val KEY_AUTH = "Authorization"
    private const val VALUE_AUTH = "5453908d-f929-44ad-8d82-731f22490fa3"

    private const val KEY_APP_ID = "Test-V4"
    private const val VALUE_APP_ID = "Test-V4"

    private const val KEY_APP_VERSION = "X-AppVersion"
    private const val VALUE_APP_VERSION = "1.0.0"

    private val apiKeyInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val requestWithKey = original.newBuilder()
            .addHeader(KEY_AUTH, "Wawi $VALUE_AUTH")
            .addHeader(KEY_APP_ID, VALUE_APP_ID)
            .addHeader(KEY_APP_VERSION, VALUE_APP_VERSION)
            .build()
        chain.proceed(requestWithKey)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val productService: ProductService =
        retrofit.create(ProductService::class.java)

    val imageService: ImageService =
        retrofit.create(ImageService::class.java)
}