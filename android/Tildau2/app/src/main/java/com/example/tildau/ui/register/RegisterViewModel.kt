package com.example.tildau.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var email: String? = null
    var name: String? = null
    var password: String? = null

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>> = _result

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> = _navigateToMain

    private val _navigateToDefect = MutableLiveData<Boolean>()
    val navigateToDefect: LiveData<Boolean> = _navigateToDefect

    suspend fun login(email: String, password: String) =
        repository.login(email, password)

    suspend fun checkDefects(): Boolean {
        return repository.checkDefects()
    }

    fun register(context: Context) {
        val email = email
        val name = name
        val password = password

        if (email.isNullOrBlank() || name.isNullOrBlank() || password.isNullOrBlank()) {
            _result.value = Result.failure(Exception("All fields are required"))
            return
        }

        viewModelScope.launch {
            try {
                repository.register(name, email, password)

                // 🔥 автологин
                val login = repository.login(email, password)
                TokenManager.saveToken(context, login.token)

                val apiWithToken = ApiClient.createServiceWithToken(
                    AuthApi::class.java
                ) { TokenManager.getToken(context) }

                val repoWithToken = AuthRepository(apiWithToken)

                val hasDefects = repoWithToken.checkDefects()

                _result.value = Result.success("OK")
                if (hasDefects) {
                    _navigateToMain.postValue(true)
                } else {
                    _navigateToDefect.postValue(true)
                }

            } catch (e: Exception) {
                _result.value = Result.failure(e)
            }
        }
    }
}