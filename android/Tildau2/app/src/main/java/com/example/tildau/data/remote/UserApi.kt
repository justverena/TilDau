package com.example.tildau.data.remote

import com.example.tildau.data.model.profile.UpdateProfileRequest
import com.example.tildau.data.model.profile.UpdateProfileResponse
import com.example.tildau.data.model.profile.UserResponse
import retrofit2.http.*

interface UserApi {

    @GET("api/me")
    suspend fun getProfile(): UserResponse

    @PUT("api/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse
}
