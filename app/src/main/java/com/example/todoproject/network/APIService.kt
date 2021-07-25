package com.example.todoproject.network

import com.example.todoproject.database.Task
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface APIService {
    @GET("/tasks/")
    suspend fun getTasks(): ArrayList<Task>

    @POST("/tasks/")
    suspend fun setTasks(@Body task : Task)

    @DELETE("/tasks/{id}/")
    suspend fun deleteTasks(@Path("id") id : String)

    @PUT("/tasks/{id}/")
    suspend fun updateTasks(@Path("id") id : String, @Body task : Task)

    companion object{
        public fun userService() : APIService {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                    val request = builder
                        .addHeader("Authorization", "Bearer 8142b4fa951c4dd99fda81aab42b745c")
                        .build()

                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(APIService::class.java)
        }
    }
}