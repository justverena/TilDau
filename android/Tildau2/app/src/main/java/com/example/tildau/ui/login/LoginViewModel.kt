package com.example.tildau.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.login.LoginResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> = _result

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> = _navigateToMain

    private val _navigateToDefect = MutableLiveData<Boolean>()
    val navigateToDefect: LiveData<Boolean> = _navigateToDefect

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                val login = repository.login(email, password)

                TokenManager.saveToken(context, login.token)

                val apiWithToken = ApiClient.createServiceWithToken(
                    AuthApi::class.java
                ) { TokenManager.getToken(context) }

                val repoWithToken = AuthRepository(apiWithToken)

                val hasDefects = repoWithToken.checkDefects()
                Log.d("CHECK_DEFECT", "hasDefects = $hasDefects")

                if (hasDefects) {
                    _navigateToMain.postValue(true)
                } else {
                    _navigateToDefect.postValue(true)
                }

            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
