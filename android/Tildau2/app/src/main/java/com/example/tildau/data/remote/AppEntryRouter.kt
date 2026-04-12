package com.example.tildau.data.remote

import android.content.Context
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.ui.login.AuthActivity
import com.example.tildau.ui.main.MainActivity
import com.example.tildau.ui.onboarding.DefectOnboardingActivity

class AppEntryRouter(
    private val authRepository: AuthRepository,
    private val context: Context
) {

    suspend fun route(): Class<*> {

        val token = TokenManager.getToken(context)

        if (token.isNullOrEmpty()) {
            return AuthActivity::class.java
        }

        return try {
            val hasDefects = authRepository.checkDefects()

            if (hasDefects) {
                MainActivity::class.java
            } else {
                DefectOnboardingActivity::class.java
            }
        } catch (e: Exception) {
            AuthActivity::class.java
        }
    }
}