package com.example.tildau.data.repository

import com.example.tildau.data.model.profile.UpdateProfileRequest
import com.example.tildau.data.model.profile.UpdateProfileResponse
import com.example.tildau.data.model.profile.UserResponse
import com.example.tildau.data.remote.UserApi

class UserRepository(
    private val api: UserApi
) {

    suspend fun getProfile(): UserResponse {
        return api.getProfile()
    }

    suspend fun updateProfile(name: String?, email: String?, password: String?): UpdateProfileResponse {
        val request = UpdateProfileRequest(name, email, password)
        return api.updateProfile(request)
    }
}
