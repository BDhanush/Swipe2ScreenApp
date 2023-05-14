package com.example.swipe2screen

import retrofit2.Call
import com.example.swipe2screen.model.Product
import retrofit2.http.GET

interface ApiInterface {
    @GET(value = "get")
    fun getProductInfo(): Call<List<Product>>
}