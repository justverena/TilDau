package com.example.tildau.data.remote

import com.example.tildau.data.model.login.LoginRequest
import com.example.tildau.data.model.login.LoginResponse
import com.example.tildau.data.model.register.RegisterRequest
import com.example.tildau.data.model.register.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
