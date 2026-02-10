package com.example.tildau.ui.profile

import androidx.lifecycle.*
import com.example.tildau.data.model.profile.UserResponse
import com.example.tildau.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse> = _user

    private val _updateResult = MutableLiveData<Result<String>>()
    val updateResult: LiveData<Result<String>> = _updateResult

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = repository.getProfile()
                _user.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfile(name: String?, email: String?, password: String?) {
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(name, email, password)
                _updateResult.value = Result.success(response.message)
                loadProfile()
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }
}
