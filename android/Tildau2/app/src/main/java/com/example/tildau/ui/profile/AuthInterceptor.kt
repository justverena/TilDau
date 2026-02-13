package com.example.tildau.ui.profile

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

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
