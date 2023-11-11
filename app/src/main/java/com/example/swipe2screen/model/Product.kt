package com.example.swipe2screen.model

//Class to store details of a product from the api get request
class Product(
    val image:String?=null,
    val price:Double,
    val product_name:String?=null,
    val product_type:String?=null,
    val tax:Double,
){}