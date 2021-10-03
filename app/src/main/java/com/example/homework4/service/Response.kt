package com.example.homework4.service

import com.example.homework4.model.User


data class Response(
    val user: User,
    val token : String
)