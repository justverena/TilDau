package com.example.tildau.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityLoginBinding
import com.example.tildau.ui.home.CoursesActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = ApiClient.createService(AuthApi::class.java)
        val repository = AuthRepository(api)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(repository) as T
                }
            }
        )[LoginViewModel::class.java]

        var isPasswordVisible = false
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.etPassword.inputType =
                if (isPasswordVisible)
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.btnTogglePassword.setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye_hide
                else R.drawable.ic_eye_show
            )
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        Toast.makeText(this, "LoginActivity started", Toast.LENGTH_SHORT).show()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        viewModel.result.observe(this) { result ->
            result.onSuccess { response ->
                val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("jwt_token", response.token)
                    .putString("user_name", response.username)
                    .apply()
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CoursesActivity::class.java))
                finish()
            }.onFailure {
                Toast.makeText(this, it.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
