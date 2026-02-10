package com.example.tildau.data.model.profile

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: String,
    val avatarUrl: String?
)
