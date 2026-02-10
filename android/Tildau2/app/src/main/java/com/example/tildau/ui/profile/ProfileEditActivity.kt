package com.example.tildau.ui.profile

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

class ProfileEditActivity : BaseActivity() {

    private lateinit var etField: EditText
    private lateinit var btnSave: Button
    private lateinit var viewModel: ProfileViewModel
    private lateinit var fieldName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContent(R.layout.activity_profile_edit)

        val tvEditTitle: TextView = findViewById(R.id.tvEditTitle)
        val tvFieldLabel: TextView = findViewById(R.id.tvFieldLabel)
        etField = findViewById(R.id.etField)
        btnSave = findViewById(R.id.btnSave)

        fieldName = intent.getStringExtra("field") ?: "name"
        val currentValue = intent.getStringExtra("currentValue") ?: ""

        when (fieldName.lowercase()) {
            "name" -> {
                tvEditTitle.text = "Edit Name"
                tvFieldLabel.text = "Name"
                etField.hint = "Enter new name"
            }
            "email" -> {
                tvEditTitle.text = "Edit Email"
                tvFieldLabel.text = "Email"
                etField.hint = "Enter new email"
            }
            "password" -> {
                tvEditTitle.text = "Edit Password"
                tvFieldLabel.text = "Password"
                etField.hint = "Enter new password"

                findViewById<LinearLayout>(R.id.passwordRepeatLayout).visibility = View.VISIBLE
                etField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        etField.setText(currentValue)
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        val api = ApiClient.createServiceWithToken(UserApi::class.java) { token }
        val repository = UserRepository(api)
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(repository) as T
                }
            }
        )[ProfileViewModel::class.java]

        btnSave.setOnClickListener {
            val newValue = etField.text.toString()
            if (newValue.isBlank()) {
                Toast.makeText(this, "Value cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var password: String? = null
            if (fieldName.lowercase() == "password") {
                val repeat = findViewById<EditText>(R.id.etFieldRepeat).text.toString()
                if (repeat != newValue) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password = newValue
            }

            val name: String? = if (fieldName.lowercase() == "name") newValue else null
            val email: String? = if (fieldName.lowercase() == "email") newValue else null

            viewModel.updateProfile(name, email, password)

            viewModel.updateResult.observe(this) { result ->
                result.onSuccess {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK, intent.apply {
                        putExtra("field", fieldName)
                        putExtra("value", newValue)
                    })
                    finish()
                }.onFailure {
                    Toast.makeText(this, it.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}
