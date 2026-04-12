package com.example.tildau.ui.main

import androidx.lifecycle.ViewModel
import com.example.tildau.data.repository.AuthRepository

class MainViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    suspend fun hasDefects(): Boolean {
        return repository.checkDefects()
    }
}