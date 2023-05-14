package com.example.swipe2screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipe2screen.adapter.ListingAdapter
import com.example.swipe2screen.databinding.ActivityMainBinding
import com.example.swipe2screen.model.Product
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
        binding.searchBar.clearFocus()

        //action on addProduct button
        binding.addProduct.setOnClickListener{
            //open upload product page by opening UploadActivity
//            Intent(this,UploadActivity::class.java).also {
//                startActivity(it)
//            }
        }

        binding.eventRecyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        binding.eventRecyclerView.layoutManager=linearLayoutManager
        getProductInfo()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.searchBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(msg)
                return false
            }
        })

    }

    private fun filter(text:String) {
        // creating a new array list to filter our data.
        val filteredlist = mutableListOf<Product>();

        // running a for loop to compare elements.
        for (item in dataset) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.product_name!!.lowercase().contains(text.lowercase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        adapter.filterList(filteredlist)

        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Product Found", Toast.LENGTH_SHORT).show();
            binding.noSearch.visibility = VISIBLE

        } else {
            binding.noSearch.visibility = INVISIBLE

        }
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
                binding.progressBar.visibility= View.GONE
                binding.eventRecyclerView.adapter=adapter

            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                return getProductInfo()

            }
        })
    }
}

