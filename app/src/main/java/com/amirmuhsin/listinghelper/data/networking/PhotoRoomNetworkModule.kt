package com.amirmuhsin.listinghelper.data.networking

import com.amirmuhsin.listinghelper.data.networking.api.PhotoRoomService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object PhotoRoomNetworkModule {

    private const val BASE_URL = "https://image-api.photoroom.com/"
    var isSandbox = true

    // Replace with your real key (or sandbox_ prefix for test)
    private const val SANDBOX_API_KEY = "sandbox_sk_pr_default_4fd9275a5e7f58371893b8130c73ee4e682fbc10"
    private const val LIVE_API_KEY = "sk_pr_default_4fd9275a5e7f58371893b8130c73ee4e682fbc10"

    private val apiKeyInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val requestWithKey = original.newBuilder()
            .addHeader("x-api-key", getApiKey())
            .build()
        chain.proceed(requestWithKey)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val photoRoomService: PhotoRoomService =
        retrofit.create(PhotoRoomService::class.java)

    private fun getApiKey(): String {
        return if (isSandbox) SANDBOX_API_KEY else LIVE_API_KEY
    }
}