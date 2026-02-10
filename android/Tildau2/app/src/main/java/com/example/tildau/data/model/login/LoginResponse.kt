package com.example.tildau.data.model.login

data class LoginResponse(
    val token: String,
    val username: String,
    val userId: Long
)
