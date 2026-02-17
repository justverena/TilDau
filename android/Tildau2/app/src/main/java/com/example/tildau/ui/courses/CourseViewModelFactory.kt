package com.example.tildau.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tildau.data.repository.CourseRepository

class CourseViewModelFactory(
    private val repository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

