package com.example.swipe2screen

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class ProductPost (

    @SerializedName("price"        ) var price       : String? = null,
    @SerializedName("product_name" ) var productName : String? = null,
    @SerializedName("product_type" ) var productType : String? = null,
    @SerializedName("tax"          ) var tax         : String? = null

)