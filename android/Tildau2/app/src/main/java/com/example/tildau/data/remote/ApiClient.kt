package com.example.tildau.data.remote

import com.example.tildau.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val client = OkHttpClient.Builder().build()

    fun clientWithToken(tokenProvider: () -> String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(service)
    }

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
        val request: Request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
