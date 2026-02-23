package com.example.tildau.ui.analyze

sealed class AnalyzeState {
    object Loading : AnalyzeState()
    object Success : AnalyzeState()
    data class Error(val message: String) : AnalyzeState()
}