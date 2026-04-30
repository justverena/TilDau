package com.example.tildau.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AppEntryRouter
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.ui.login.AuthActivity
import com.example.tildau.ui.main.MainActivity
import com.example.tildau.ui.onboarding.DefectOnboardingActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            val api = ApiClient.createServiceWithToken(
                AuthApi::class.java
            ) { TokenManager.getToken(this@SplashActivity) }

            val repository = AuthRepository(api)

            val router = AppEntryRouter(repository, this@SplashActivity)

            val nextScreen = try {
                router.route()
            } catch (e: Exception) {
                AuthActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, nextScreen))
            finish()
        }
    }
}