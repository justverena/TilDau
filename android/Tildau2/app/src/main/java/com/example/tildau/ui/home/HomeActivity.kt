package com.example.tildau.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.databinding.ActivityHomeBinding
import com.example.tildau.ui.profile.ProfileActivity
import com.example.tildau.data.remote.UserApi
import com.example.tildau.data.repository.UserRepository
import com.example.tildau.ui.base.BaseActivity
import com.example.tildau.ui.profile.AccountActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setBaseContent(R.layout.activity_home)

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}

