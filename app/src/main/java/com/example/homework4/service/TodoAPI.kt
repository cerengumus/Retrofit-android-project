package com.example.homework4.service

import com.example.homework4.fragments.HomeFragment
import com.example.homework4.model.User
import retrofit2.Call
import retrofit2.http.*

interface TodoAPI {
    @POST("user/login")
    fun login(@Body loginRequest: Request) : Call<Response>

    @GET("user/me")
    fun getMe() : Call<User>

    @POST("task")
    fun addTask(@Body todo: Todo): Call<Unit>

    @GET("task")
    fun getAllTasks(): Call<GetResponse>

    @GET("task/{id}")
    fun getTaskById(@Path("id") id :Int): Call<Todo>

    @GET("task?completed=true")
    fun getTaskByCompleted(): Call<ArrayList<Todo>>

    @GET("task")
    fun getTaskByPagination(@Query("limit") limit: Int, @Query("skip") skip: Int): Call<GetResponse>

    @PUT("task/{id}")
    fun updateTaskById(@Path("id") id: String,  @Body completed: HomeFragment.CompleteBody): Call<Unit>

    @DELETE("task/{id}")
    fun deleteTaskById(@Path("id") id: String): Call<Unit>
}