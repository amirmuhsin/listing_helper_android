package com.amirmuhsin.listinghelper.data.networking

import com.amirmuhsin.listinghelper.data.networking.api.ImageService
import com.amirmuhsin.listinghelper.data.networking.api.ProductService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ProductNetworkModule {

    private const val BASE_URL = "https://87.106.214.160:6553/api/eazybusiness/"

    private const val KEY_AUTH = "Authorization"
    private const val VALUE_AUTH = "795e6359-05b3-4270-9eb0-84710223f105"

    private const val KEY_APP_ID = "X-AppId"
    private const val VALUE_APP_ID = "Test-V7"

    private const val KEY_APP_VERSION = "X-AppVersion"
    private const val VALUE_APP_VERSION = "1.0.7"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(getUnsafeOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productService: ProductService =
        retrofit.create(ProductService::class.java)

    val imageService: ImageService =
        retrofit.create(ImageService::class.java)

    fun getUnsafeOkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val requestWithKey = original.newBuilder()
                .addHeader(KEY_AUTH, "Wawi $VALUE_AUTH")
                .addHeader(KEY_APP_ID, VALUE_APP_ID)
                .addHeader(KEY_APP_VERSION, VALUE_APP_VERSION)
                .build()
            chain.proceed(requestWithKey)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        // Trust manager that does NOT validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object: X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            // Timeout configurations to prevent crashes
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)
            .build()
    }
}