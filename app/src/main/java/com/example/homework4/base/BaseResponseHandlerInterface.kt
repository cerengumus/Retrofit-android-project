package com.example.homework4.base

interface BaseResponseHandlerInterface<T> {

    fun onSuccess(data :  T)
    fun onFailure()

}