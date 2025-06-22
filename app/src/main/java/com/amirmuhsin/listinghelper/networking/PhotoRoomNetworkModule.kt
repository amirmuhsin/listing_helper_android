package com.amirmuhsin.listinghelper.networking

import com.amirmuhsin.listinghelper.networking.api.PhotoRoomService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object PhotoRoomNetworkModule {

    private const val BASE_URL = "https://image-api.photoroom.com/"

    // Replace with your real key (or sandbox_ prefix for test)
    private const val SANDBOX_API_KEY = "sandbox_sk_pr_74e5a0c21657ff03d7ab5fbb600af85de5e18b2b"
    private const val LIVE_API_KEY = "sk_pr_default_77bc8dcb1351f648fe972ef949ecb8e2abb950d3"

    private val apiKeyInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val requestWithKey = original.newBuilder()
            .addHeader("x-api-key", SANDBOX_API_KEY)
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
}