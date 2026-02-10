package com.example.tildau.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityRegisterEmailBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterEmailBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = Retrofit.Builder()
            .baseUrl("http://192.168.1.106:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

        val repository = AuthRepository(api)
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        binding.etEmail.setText(viewModel.email)

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RegisterNameActivity::class.java)
            intent.putExtra("email", binding.etEmail.text.toString())
            startActivity(intent)
        }

    }
}
