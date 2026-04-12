package com.example.tildau.data.repository

import android.util.Log
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.model.course.CourseShortResponse
import com.example.tildau.data.model.next.NextStepResponse
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

        Log.d("API", "code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            Log.d("API", "body: $body")
            return body
        } else {
            Log.e("API", "error: ${response.errorBody()?.string()}")
            return null
        }
    }

    suspend fun startCourse(id: String): NextStepResponse? {
        return try {
            api.startCourse(id)
        } catch (e: Exception) {
            Log.e("CourseRepository", "Failed to start course", e)
            null
        }
    }

    suspend fun resumeCourse(id: String): NextStepResponse? {
        return try {
            api.resumeCourse(id)
        } catch (e: Exception) {
            Log.e("CourseRepository", "Failed to resume course", e)
            null
        }
    }
}
