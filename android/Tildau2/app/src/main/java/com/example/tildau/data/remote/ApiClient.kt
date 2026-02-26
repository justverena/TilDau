package com.example.tildau.data.remote

import android.util.Log
import com.example.tildau.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Клиент без токена
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS) // соединение до 2 минут
        .readTimeout(120, TimeUnit.SECONDS)    // чтение ответа до 2 минут
        .writeTimeout(120, TimeUnit.SECONDS)   // отправка данных до 2 минут
        .build()

    // Клиент с токеном
    fun clientWithToken(tokenProvider: () -> String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    // Сервис без токена
    fun <T> createService(service: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(service)
    }

    // Сервис с токеном
    fun <T> createServiceWithToken(service: Class<T>, tokenProvider: () -> String?): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientWithToken(tokenProvider))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
            Log.d("AuthInterceptor", "Token added to request: $token")
        } else {
            Log.d("AuthInterceptor", "No token found! Request sent without Authorization header")
        }

        val requestWithToken = requestBuilder.build()
        Log.d("AuthInterceptor", "Sending request to: ${requestWithToken.url}")
        return chain.proceed(requestWithToken)
    }
}