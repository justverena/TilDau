package com.example.tildau.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityRegisterPasswordBinding
import com.example.tildau.ui.main.MainActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPasswordBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = Retrofit.Builder()
            .baseUrl("http://192.168.1.106:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

        val repository = AuthRepository(api)
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        binding.etPassword.setText(viewModel.password)

        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")

        viewModel.result.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            result.onFailure { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnContinue.setOnClickListener {
            viewModel.email = email
            viewModel.name = name
            viewModel.password = binding.etPassword.text.toString()

            viewModel.register()
        }
    }
}
