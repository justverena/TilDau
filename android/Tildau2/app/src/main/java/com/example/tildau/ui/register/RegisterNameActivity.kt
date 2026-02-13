// RegisterNameActivity.kt
package com.example.tildau.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.databinding.ActivityRegisterNameBinding

class RegisterNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterNameBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = ApiClient.createService(AuthApi::class.java)
        val repository = AuthRepository(api)
        val factory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        binding.etName.setText(viewModel.name)

        val email = intent.getStringExtra("email") ?: ""

        binding.btnNext.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name.isBlank()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, RegisterPasswordActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}
