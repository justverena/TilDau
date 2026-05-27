package com.example.tildau.ui.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.exercise.ExerciseFullResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.ExerciseApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    // =========================
    // STATE
    // =========================
    enum class RecordingState {
        IDLE,
        RECORDING,
        FINISHED,
        PLAYING
    }

    private val _uiState = MutableStateFlow(RecordingState.IDLE)
    val uiState: StateFlow<RecordingState> = _uiState

    fun setState(state: RecordingState) {
        _uiState.value = state
    }

    // =========================
    // EXERCISE DATA
    // =========================
    private val _exercise = MutableStateFlow<ExerciseFullResponse?>(null)
    val exercise: StateFlow<ExerciseFullResponse?> = _exercise

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadExercise(exerciseId: String) {
        viewModelScope.launch {
            try {
                val api = ApiClient.createServiceWithToken(ExerciseApi::class.java) {
                    TokenManager.getToken(getApplication())
                }

                val result = api.getExercise(exerciseId)
                _exercise.value = result

            } catch (e: Exception) {
                _error.value = "Failed to load exercise"
                android.util.Log.e("RecordViewModel", "Error loading exercise", e)
            }
        }
    }
}