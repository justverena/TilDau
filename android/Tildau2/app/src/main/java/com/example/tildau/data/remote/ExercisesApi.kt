package com.example.tildau.data.remote

import com.example.tildau.data.model.exercise.ExerciseFullResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExerciseApi {
    @GET("api/exercises/{id}")
    suspend fun getExercise(
        @Path("id") exerciseId: String
    ): ExerciseFullResponse
}
