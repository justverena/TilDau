package com.example.tildau.ui.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyzeViewModel : ViewModel() {

    private val _state = MutableStateFlow<AnalyzeState>(AnalyzeState.Loading)
    val state: StateFlow<AnalyzeState> = _state

    fun analyze(audioPath: String) {
        viewModelScope.launch {
            _state.value = AnalyzeState.Loading

            try {
                // TODO: Replace with real backend call
                delay(3000)

                _state.value = AnalyzeState.Success
            } catch (e: Exception) {
                _state.value = AnalyzeState.Error(e.message ?: "Unknown error")
            }
        }
    }
}