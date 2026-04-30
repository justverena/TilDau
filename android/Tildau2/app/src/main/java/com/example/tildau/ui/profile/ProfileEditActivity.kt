package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.UserApi
import com.example.tildau.data.repository.UserRepository
import com.example.tildau.ui.base.BaseActivity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var etField: EditText
    private lateinit var btnSave: Button
    private lateinit var viewModel: ProfileViewModel
    private lateinit var fieldName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        val tvEditTitle: TextView = findViewById(R.id.tvEditTitle)
        val tvFieldLabel: TextView = findViewById(R.id.tvFieldLabel)
        etField = findViewById(R.id.etField)
        btnSave = findViewById(R.id.btnSave)

        fieldName = intent.getStringExtra("field") ?: "name"
        val currentValue = intent.getStringExtra("currentValue") ?: ""

        setupUI(tvEditTitle, tvFieldLabel)

        etField.setText(currentValue)

        initViewModel()
        observeResult()

        btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun initViewModel() {
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
    }

    private fun observeResult() {
        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                setResult(RESULT_OK, Intent().apply {
                    putExtra("field", fieldName)
                    putExtra("value", etField.text.toString())
                })
                finish()
            }.onFailure {
                Toast.makeText(this, it.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData() {
        val value = etField.text.toString()

        if (value.isBlank()) {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show()
            return
        }

        val name = if (fieldName == "name") value else null
        val email = if (fieldName == "email") value else null
        val password = if (fieldName == "password") value else null

        viewModel.updateProfile(name, email, password)
    }

    private fun setupUI(title: TextView, label: TextView) {
        when (fieldName) {
            "name" -> {
                title.text = "Edit Name"
                label.text = "Name"
            }
            "email" -> {
                title.text = "Edit Email"
                label.text = "Email"
            }
            "password" -> {
                title.text = "Edit Password"
                label.text = "Password"
            }
        }
    }
}