package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.example.tildau.R
import com.example.tildau.ui.base.BaseActivity

class ProfileViewActivity : BaseActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView

    private val editFieldLauncher =
        registerForActivityResult(ProfileEditContract()) { result ->
            // result: Pair<String, String> = field -> newValue
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
        setBaseContent(R.layout.activity_profile_view)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPassword = findViewById(R.id.tvPassword)

        // TODO: подтянуть реальные данные с бэка
        loadUserData()

        makeFieldClickable(tvName, "name")
        makeFieldClickable(tvEmail, "email")
        makeFieldClickable(tvPassword, "password")
    }

    private fun loadUserData() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        val api = com.example.tildau.data.remote.ApiClient.createServiceWithToken(
            com.example.tildau.data.remote.UserApi::class.java
        ) { token }

        val repository = com.example.tildau.data.repository.UserRepository(api)
        val viewModel = com.example.tildau.ui.profile.ProfileViewModel(repository)

        viewModel.loadProfile()
        viewModel.user.observe(this) { user ->
            tvName.text = user.name
            tvEmail.text = user.email
            tvPassword.text = "********"
        }
    }

    private fun makeFieldClickable(textView: TextView, field: String) {
        textView.isClickable = true
        textView.isFocusable = true
        ViewCompat.setBackground(textView, getDrawable(R.drawable.ripple_effect))
        textView.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra("field", field)
            intent.putExtra("currentValue", textView.text.toString())
            editFieldLauncher.launch(intent)
        }
    }
}
