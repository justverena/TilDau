package com.example.tildau.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.model.course.CourseShortResponse
import com.example.tildau.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repository: CourseRepository
) : ViewModel() {

    private val _courses = MutableLiveData<List<CourseShortResponse>>()
    val courses: LiveData<List<CourseShortResponse>> = _courses

    private val _selectedCourse = MutableLiveData<CourseFullResponse>()
    val selectedCourse: LiveData<CourseFullResponse> = _selectedCourse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCourses() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getCourses()
                if (result != null) {
                    _courses.value = result
                } else {
                    _courses.value = emptyList()
                    _error.value = "Failed to load courses"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error: ${e.message}"
                _courses.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadCourseById(id: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCourseById(id)
                if (result != null) {
                    _selectedCourse.value = result
                } else {
                    _error.value = "Course not found"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error: ${e.message}"
            }
        }
    }
}
