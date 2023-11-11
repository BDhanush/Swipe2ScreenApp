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

//retrofit api service
interface ApiInterface {
    //a method to make an HTTP GET request to retrieve a list of products from the "get" endpoint
    @GET(value = "get")
    fun getProductInfo(): Call<List<Product>>

    //a method for sending a multipart form data POST request to the "add" endpoint
    @Multipart
    @POST(value = "add")
    fun upload(
        @Part("price") price: RequestBody,
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part image: MultipartBody.Part?=null
    ):Call<ResponseBody>
}