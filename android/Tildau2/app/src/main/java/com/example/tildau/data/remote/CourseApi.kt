package com.example.tildau.data.remote

import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.model.course.CourseShortResponse
import com.example.tildau.data.model.exercise.ExerciseFullResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseApi {

    @GET("api/courses")
    suspend fun getCourses(): Response<List<CourseShortResponse>>

    @GET("api/courses/{id}")
    suspend fun getCourseById(
        @Path("id") courseId: String
    ): Response<CourseFullResponse>

    @GET("api/exercises/{id}")
    suspend fun getExercise(
        @Path("id") exerciseId: String
    ): ExerciseFullResponse
}
