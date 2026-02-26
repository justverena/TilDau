package com.example.tildau.data.remote

import com.example.tildau.data.model.exercise.ExerciseFullResponse
import com.example.tildau.data.model.exercise.SubmitExerciseResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ExerciseApi {
    @GET("api/exercises/{id}")
    suspend fun getExercise(
        @Path("id") exerciseId: String
    ): ExerciseFullResponse

    @Multipart
    @POST("api/exercises/{id}/submit")
    suspend fun submitExercise(
        @Path("id") exerciseId: String,
        @Part file: MultipartBody.Part
    ): SubmitExerciseResponse
}
