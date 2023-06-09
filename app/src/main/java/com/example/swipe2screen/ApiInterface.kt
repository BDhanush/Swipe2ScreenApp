package com.example.swipe2screen

import retrofit2.Call
import com.example.swipe2screen.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {
    @GET(value = "get")
    fun getProductInfo(): Call<List<Product>>

    @Multipart
    @POST(value = "add")
    suspend fun postProductInfo(
        @Part("image") image: RequestBody,
        @Part("price") price: RequestBody,
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("tax") tax: RequestBody
    ):ResponseBody
}