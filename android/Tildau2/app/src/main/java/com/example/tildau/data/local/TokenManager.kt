package com.example.tildau.data.local

import android.content.Context
import android.util.Log

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "jwt_token"

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TOKEN_KEY, token)
            .apply()
        Log.d("TokenManager", "Token saved: $token")
    }

    fun getToken(context: Context): String? {
        val token = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, null)
        Log.d("TokenManager", "getToken() returned: $token")
        return token
    }
}
