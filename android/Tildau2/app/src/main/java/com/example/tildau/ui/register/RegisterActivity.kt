package com.example.tildau.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrofit + ApiClient
        val api = ApiClient.createService(AuthApi::class.java)
        val repository = AuthRepository(api)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return RegisterViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)
            .get(RegisterViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            viewModel.name = binding.etName.text.toString()
            viewModel.email = binding.etEmail.text.toString()
            viewModel.password = binding.etPassword.text.toString()
            viewModel.register()
        }

        viewModel.result.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, it.message ?: "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
