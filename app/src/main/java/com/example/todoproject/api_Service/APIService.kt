package com.example.todoproject.api_Service

import com.example.todoproject.dao.Task
import retrofit2.http.*

interface APIService {
    @GET("/tasks/")
    suspend fun getTasks(): ArrayList<Task>

    @POST("/tasks/")
    suspend fun setTasks(@Body task : Task)

    @DELETE("/tasks/{id}/")
    suspend fun deleteTasks(@Path("id") id : String)

    @PUT("/tasks/{id}/")
    suspend fun updateTasks(@Path("id") id : String, @Body task : Task)

}