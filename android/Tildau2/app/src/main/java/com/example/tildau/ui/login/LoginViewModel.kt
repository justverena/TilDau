package com.example.tildau.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.model.login.LoginResponse
import com.example.tildau.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> = _result

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                _result.value = Result.success(response)
            } catch (e: Exception) {
                _result.value = Result.failure(e)
            }
        }
    }
}
