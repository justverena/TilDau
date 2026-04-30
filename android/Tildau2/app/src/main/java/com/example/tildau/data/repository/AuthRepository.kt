package com.example.tildau.data.repository

import com.example.tildau.data.model.defect.SetDefectRequest
import com.example.tildau.data.model.login.LoginRequest
import com.example.tildau.data.model.login.LoginResponse
import com.example.tildau.data.model.register.RegisterRequest
import com.example.tildau.data.model.register.RegisterResponse
import com.example.tildau.data.remote.AuthApi

class AuthRepository(
    private val api: AuthApi
) {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return api.register(
            RegisterRequest(
                name = name,
                email = email,
                password = password
            )
        )
    }

    suspend fun getDefectTypes() =
        api.getDefectTypes()

    suspend fun setDefect(id: Int) {
        android.util.Log.d("DEFECT_FLOW", "API CALL setDefect id=$id")
        api.setDefect(SetDefectRequest(id))
    }

    suspend fun checkDefects(): Boolean {
        return api.getDefectStatus().hasDefects
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return api.login(LoginRequest(email, password))
    }
}
