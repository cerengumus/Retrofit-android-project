package com.example.homework4.service

import com.example.homework4.model.User
import com.patikadev.deneme1.utils.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceConnector {

    companion object {
        private var retrofit: Retrofit? = null
        lateinit var restInterface: TodoAPI

        fun init() {

            //logging interceptor
            val logging = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            //adding logging interceptor to okhttp
            val httpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthenticationInterceptor())
                .build()

            //creating retrofit client to correspond to back-end service
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()


            restInterface = retrofit?.create(TodoAPI::class.java)!!



        }
    }


    class AuthenticationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", User.getCurrentInstance().token ?: "")
                .build()

            return chain.proceed(request)
        }
    }


}