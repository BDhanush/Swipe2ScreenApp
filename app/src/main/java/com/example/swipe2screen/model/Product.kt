package com.example.swipe2screen.model

//Class to store details of a product from the api get request
class Product(
    val image:String?=null,
    val price:Double,
    var product_name:String?=null,
    var product_type:String?=null,
    val tax:Double,
){}