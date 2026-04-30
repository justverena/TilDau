package com.example.tildau.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository



class DefectViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    suspend fun setDefect(id: Int) {
        repository.setDefect(id)
    }

    suspend fun checkDefects(): Boolean {
        return repository.checkDefects()
    }
}