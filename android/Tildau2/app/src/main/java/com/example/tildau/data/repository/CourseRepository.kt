package com.example.tildau.data.repository

import android.util.Log
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.model.course.CourseShortResponse
import com.example.tildau.data.remote.CourseApi

class CourseRepository(private val api: CourseApi) {

    suspend fun getCourses(): List<CourseShortResponse>? {
        val response = api.getCourses()
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("CourseRepository", "Failed to fetch courses: ${response.code()}")
            null
        }
    }

    suspend fun getCourseById(id: String): CourseFullResponse? {
        val response = api.getCourseById(id)
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("CourseRepository", "Failed to fetch course $id: ${response.code()}")
            null
        }
    }
}
