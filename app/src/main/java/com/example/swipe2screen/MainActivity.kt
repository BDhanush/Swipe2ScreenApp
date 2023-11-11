package com.example.swipe2screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipe2screen.adapter.ListingAdapter
import com.example.swipe2screen.databinding.ActivityMainBinding
import com.example.swipe2screen.model.Product
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


const val dataURL="https://app.getswipe.in/api/public/"                 //api link

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ListingAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var dataset=mutableListOf<Product>()                                //dataset of products

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.progressBar.show()              //loading indicator (progress bar)

        //open upload activity when clicked on addProduct button (bottom right hand corner)
        binding.addProduct.setOnClickListener{
            //open upload product page by opening UploadActivity
            Intent(this,UploadActivity::class.java).also {
                startActivity(it)
            }
        }

        //initialize Products RecyclerView
        binding.recyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        binding.recyclerView.layoutManager=linearLayoutManager
        getProductInfo()        //make get call and load products
        //actions pull down refresh
        binding.swipeRefreshLayout.setOnRefreshListener{
            binding.swipeRefreshLayout.isRefreshing = false
            getProductInfo()
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.clearFocus()

        //search based on changing text
        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                // filter dataset from input
                filter(s.toString())
            }
        })

        binding.searchView.editText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            binding.searchBar.setText(binding.searchView.text)
            binding.searchView.hide()
            false
        }


    }

    //to filter dataset and perform search
    private fun filter(searchString:String) {
        //creating a new list to filter dataset.
        val filteredList = mutableListOf<Product>()

        //running a loop to compare elements.
        for (item in dataset) {
            //checking if the entered string matched with any item of our recycler view.
            if (item.product_name!!.contains(searchString, true) || item.product_type!!.contains(searchString, true)) {
                //if the item is matched we are add it to filtered list
                filteredList.add(item)
            }
        }
        // if no item is added in filtered list, show "No Product Found" TextView
        if (filteredList.isEmpty()) {
            binding.noSearch.visibility = VISIBLE

        } else {
            binding.noSearch.visibility = INVISIBLE

        }
        Log.i("check", filteredList.toString())
        //set new dataset
        adapter = ListingAdapter(applicationContext,filteredList)
        binding.recyclerView.adapter=adapter
    }


    private fun getProductInfo()
    {
        val retrofitBuffer=Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(dataURL).build().create(ApiInterface::class.java)

        //make an api call to get products list
        val retrofitData = retrofitBuffer.getProductInfo()

        retrofitData.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(call: Call<List<Product>?>, response: Response<List<Product>?>) {
                val responseBody=response.body()!!      //returns a list of Products
                dataset=responseBody.toMutableList()
                //set recycler view
                adapter = ListingAdapter(baseContext,dataset)
                adapter.notifyDataSetChanged()
                binding.progressBar.hide()
                binding.recyclerView.adapter=adapter
                binding.swipeRefreshLayout.isRefreshing=false

            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
//                return getProductInfo()

            }
        })
    }
    override fun onBackPressed() {
        //if search bar is selected close it first when clicked on back button
        if (binding.searchView.isShowing || binding.searchBar.text.isNotEmpty()) {
            binding.searchView.setText("")
            binding.searchView.hide()
            binding.searchBar.clearText()
            return
        }
        super.onBackPressed()

    }
}

