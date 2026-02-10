package com.example.tildau.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun register() {
        val email = email
        val name = name
        val password = password

        if (email.isNullOrBlank() || name.isNullOrBlank() || password.isNullOrBlank()) {
            _result.value = Result.failure(Exception("All fields are required"))
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                _result.value = Result.success(response.message)
            } catch (e: Exception) {
                _result.value = Result.failure(e)
            }
        }
    }
}
