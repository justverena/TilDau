package com.example.tildau.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tildau.R
import com.example.tildau.ui.base.BaseActivity
import com.example.tildau.ui.login.LoginActivity

class AccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContent(R.layout.activity_account)

        val userNameText = findViewById<TextView>(R.id.userNameText)

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User")

        userNameText.text = userName

        val profileRow = findViewById<LinearLayout>(R.id.profileRow)
        val logoutButton = findViewById<Button>(R.id.deleteButton)

        profileRow.setOnClickListener {
            startActivity(Intent(this, ProfileViewActivity::class.java))
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}

