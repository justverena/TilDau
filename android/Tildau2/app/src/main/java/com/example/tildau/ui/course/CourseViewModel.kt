package com.example.tildau.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.model.course.UnitResponse
import com.example.tildau.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _selectedCourse = MutableLiveData<CourseFullResponse>()
    val selectedCourse: LiveData<CourseFullResponse> = _selectedCourse

    private val _selectedUnit = MutableLiveData<UnitResponse>()
    val selectedUnit: LiveData<UnitResponse> = _selectedUnit

    fun loadCourseById(courseId: String) {
        viewModelScope.launch {
            repository.getCourseById(courseId)?.let {
                _selectedCourse.postValue(it)
            }
        }
    }

    fun selectUnit(unit: UnitResponse) {
        _selectedUnit.value = unit
    }
}
