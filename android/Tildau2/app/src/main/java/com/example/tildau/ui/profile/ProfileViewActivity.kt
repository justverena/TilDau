package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.ui.base.BaseActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.remote.UserApi
import com.example.tildau.data.repository.UserRepository

class ProfileViewActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView
    private lateinit var viewModel: ProfileViewModel

    private val editFieldLauncher =
        registerForActivityResult(ProfileEditContract()) { result ->
            result?.let { (field, value) ->
                when (field) {
                    "name" -> tvName.text = value
                    "email" -> tvEmail.text = value
                    "password" -> tvPassword.text = "********"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPassword = findViewById(R.id.tvPassword)

        initViewModel()
        loadUserData()

        makeFieldClickable(tvName, "name")
        makeFieldClickable(tvEmail, "email")
        makeFieldClickable(tvPassword, "password")
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

    private fun loadUserData() {
        viewModel.loadProfile()
        viewModel.user.observe(this) { user ->
            tvName.text = user.name
            tvEmail.text = user.email
            tvPassword.text = "********"
        }
    }

    private fun makeFieldClickable(textView: TextView, field: String) {
        textView.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra("field", field)
            intent.putExtra("currentValue", textView.text.toString())
            editFieldLauncher.launch(intent)
        }
    }
}