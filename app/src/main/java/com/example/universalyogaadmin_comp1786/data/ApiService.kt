package com.example.universalyogaadmin_comp1786.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("upload")
    suspend fun uploadData(@Body data: UploadData): Response<Unit>
}

data class UploadData(val courses: List<Course>, val instances: List<Instance>)