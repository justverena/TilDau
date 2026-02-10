package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.UserApi
import com.example.tildau.data.repository.UserRepository
import com.example.tildau.databinding.ActivityProfileBinding
import com.example.tildau.ui.base.BaseActivity

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setBaseContent(R.layout.activity_profile)

        val nameFromView = intent.getStringExtra("name")
        val emailFromView = intent.getStringExtra("email")

        binding.etName.setText(nameFromView)
        binding.etEmail.setText(emailFromView)

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        val api = ApiClient.createServiceWithToken(UserApi::class.java) { token }
        val repository = UserRepository(api)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(repository) as T
                }
            }
        )[ProfileViewModel::class.java]

        viewModel.loadProfile()
        viewModel.user.observe(this) { user ->
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
        }

        binding.btnSave.setOnClickListener {
            viewModel.updateProfile(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString().takeIf { it.isNotBlank() }
            )
        }

        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                val data = Intent().apply {
                    putExtra("name", binding.etName.text.toString())
                    putExtra("email", binding.etEmail.text.toString())
                }
                setResult(RESULT_OK, data)
                finish()
            }.onFailure {
                Toast.makeText(this, it.message ?: "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
