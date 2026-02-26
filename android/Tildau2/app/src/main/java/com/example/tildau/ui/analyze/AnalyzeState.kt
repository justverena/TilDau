package com.example.tildau.ui.analyze

import com.example.tildau.data.model.exercise.SubmitExerciseResponse

sealed class AnalyzeState {
    object Idle : AnalyzeState()
    object Loading : AnalyzeState()
    data class Success(val result: SubmitExerciseResponse) : AnalyzeState()
    data class Error(val message: String) : AnalyzeState()
}