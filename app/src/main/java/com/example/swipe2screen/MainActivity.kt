package com.example.swipe2screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swipe2screen.adapter.ListingAdapter
import com.example.swipe2screen.databinding.ActivityMainBinding
import com.example.swipe2screen.model.Product
import com.google.android.material.search.SearchBar
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


val dataURL="https://app.getswipe.in/api/public/"                 //api link

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ListingAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var dataset=mutableListOf<Product>()

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.progressBar.show()
        //action on addProduct button
        binding.addProduct.setOnClickListener{
            //open upload product page by opening UploadActivity
            Intent(this,UploadActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.recyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        binding.recyclerView.layoutManager=linearLayoutManager
        getProductInfo()
        binding.swipeRefreshLayout.setOnRefreshListener{
            binding.swipeRefreshLayout.isRefreshing = false
            getProductInfo()
        }

        val searchView: com.google.android.material.search.SearchView = findViewById(R.id.searchView)
        val searchBar: SearchBar = findViewById(R.id.searchBar)
        searchView.setupWithSearchBar(searchBar)
        searchView.clearFocus()

        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                filter(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })

        searchView.editText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            searchBar.text = searchView.text
            searchView.hide()
            false
        }


    }

    private fun filter(searchString:String) {
        // creating a new array list to filter our data.
        val filteredList = mutableListOf<Product>()

        // running a for loop to compare elements.
        for (item in dataset) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.product_name!!.contains(searchString, true) || item.product_type!!.contains(searchString, true)) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Product Found", Toast.LENGTH_SHORT).show();
            binding.noSearch.visibility = VISIBLE

        } else {
            binding.noSearch.visibility = INVISIBLE

        }
        Log.i("check", filteredList.toString())
        adapter = ListingAdapter(applicationContext,filteredList)
        binding.recyclerView.adapter=adapter
    }


    private fun getProductInfo()
    {
        val retrofitBuffer=Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(dataURL).build().create(ApiInterface::class.java)

        val retrofitData = retrofitBuffer.getProductInfo()

        retrofitData.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(call: Call<List<Product>?>, response: Response<List<Product>?>) {
                val responseBody=response.body()!!
                dataset=responseBody.toMutableList()
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
        if (binding.searchView.isShowing) {
            binding.searchView.setText("")
            binding.searchView.hide()
            binding.searchBar.clearText()
            return
        }
        super.onBackPressed()

    }
}

