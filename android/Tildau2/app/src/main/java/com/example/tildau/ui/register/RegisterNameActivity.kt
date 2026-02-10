package com.example.tildau.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityRegisterNameBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterNameBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = Retrofit.Builder()
            .baseUrl("http://192.168.1.106:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

        val repository = AuthRepository(api)
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        binding.etName.setText(viewModel.name)

        val email = intent.getStringExtra("email")

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RegisterPasswordActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("name", binding.etName.text.toString())
            startActivity(intent)
        }

    }
}
