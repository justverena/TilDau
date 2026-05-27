package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.UserApi
import com.example.tildau.data.repository.UserRepository

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
        val passwordRepeatLayout: LinearLayout = findViewById(R.id.passwordRepeatLayout)
        val etRepeat: EditText = findViewById(R.id.etFieldRepeat)

        etField = findViewById(R.id.etField)
        btnSave = findViewById(R.id.btnSave)

        fieldName = intent.getStringExtra("field") ?: "name"
        val currentValue = intent.getStringExtra("currentValue") ?: ""

        // UI настройка
        when (fieldName) {
            "name" -> {
                tvEditTitle.text = "Edit Name"
                tvFieldLabel.text = "Name"
            }
            "email" -> {
                tvEditTitle.text = "Edit Email"
                tvFieldLabel.text = "Email"
            }
            "password" -> {
                tvEditTitle.text = "Edit Password"
                tvFieldLabel.text = "Password"
                passwordRepeatLayout.visibility = View.VISIBLE
                etField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        etField.setText(currentValue)

        initViewModel()

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

        btnSave.setOnClickListener {
            val value = etField.text.toString()

            if (value.isBlank()) {
                Toast.makeText(this, "Field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fieldName == "password") {
                val repeat = etRepeat.text.toString()
                if (repeat != value) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val name = if (fieldName == "name") value else null
            val email = if (fieldName == "email") value else null
            val password = if (fieldName == "password") value else null

            viewModel.updateProfile(name, email, password)
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
}