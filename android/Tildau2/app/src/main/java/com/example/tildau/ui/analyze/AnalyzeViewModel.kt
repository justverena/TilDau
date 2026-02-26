package com.example.tildau.ui.analyze

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.ExerciseApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AnalyzeViewModel : ViewModel() {

    private val _state = MutableStateFlow<AnalyzeState>(AnalyzeState.Idle)
    val state: StateFlow<AnalyzeState> = _state

    fun analyze(audioPath: String, exerciseId: String, context: Context) {
        viewModelScope.launch {
            _state.value = AnalyzeState.Loading

            try {
                val file = File(audioPath)
                if (!file.exists()) {
                    _state.value = AnalyzeState.Error("Audio file not found")
                    return@launch
                }

                val requestFile =
                    file.asRequestBody("audio/wav".toMediaTypeOrNull())

                val body =
                    MultipartBody.Part.createFormData("file", file.name, requestFile)

                val api = ApiClient.createServiceWithToken(
                    ExerciseApi::class.java
                ) {
                    TokenManager.getToken(context)
                }

                val response = api.submitExercise(exerciseId, body)

                _state.value = AnalyzeState.Success(response)

            } catch (e: Exception) {
                Log.e("AnalyzeViewModel", "Error submitting audio", e)
                _state.value = AnalyzeState.Error(e.message ?: "Unknown error")
            }
        }
    }
}