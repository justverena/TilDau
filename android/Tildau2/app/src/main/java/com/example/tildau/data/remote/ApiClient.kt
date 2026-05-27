package com.example.tildau.data.remote

import android.util.Log
import com.example.tildau.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val TAG = "API_DEBUG"

    // Логирование HTTP
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Клиент без токена
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    fun provideStatisticsApi(tokenProvider: () -> String?): StatisticsApi {
        return createServiceWithToken(StatisticsApi::class.java, tokenProvider)
    }

    // Клиент с токеном
    fun clientWithToken(tokenProvider: () -> String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    // Сервис без токена
    fun <T> createService(service: Class<T>): T {

        Log.d(TAG, "BASE_URL = ${BuildConfig.BASE_URL}")

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d(TAG, "Retrofit created successfully")

        return retrofit.create(service)
    }

    // Сервис с токеном
    fun <T> createServiceWithToken(service: Class<T>, tokenProvider: () -> String?): T {

        Log.d(TAG, "BASE_URL WITH TOKEN = ${BuildConfig.BASE_URL}")

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientWithToken(tokenProvider))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d(TAG, "Retrofit with token created successfully")

        return retrofit.create(service)
    }
}

// AuthInterceptor
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

        val token = tokenProvider()

        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("API_DEBUG", "Token added")
        } else {
            Log.d("API_DEBUG", "No token found")
        }

        val requestWithToken = requestBuilder.build()

        Log.d("API_DEBUG", "Sending request to: ${requestWithToken.url}")

        return chain.proceed(requestWithToken)
    }
}