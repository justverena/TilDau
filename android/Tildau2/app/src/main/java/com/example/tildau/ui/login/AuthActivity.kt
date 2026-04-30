package com.example.tildau.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tildau.R
import com.example.tildau.databinding.ActivityAuthBinding
import com.example.tildau.ui.main.MainActivity
import com.example.tildau.ui.register.RegisterEmailActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterEmailActivity::class.java)
            startActivity(intent)
        }
    }



//    override fun onStart() {
//        super.onStart()
//
//        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
//        val token = prefs.getString("jwt_token", null)
//
//        if (!token.isNullOrEmpty()) {
//            // Если уже залогинен — сразу в MainActivity
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
//        }
//    }


}
