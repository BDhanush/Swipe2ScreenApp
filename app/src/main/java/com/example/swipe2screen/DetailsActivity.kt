package com.example.swipe2screen

import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.example.swipe2screen.databinding.ActivityDetailsBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val extras = intent.extras

        if (extras != null) {
            //load data from intent extras
            binding.title.text = extras.getString("productName")
            binding.type.text = extras.getString("productType")
            val price = intent.getDoubleExtra("price",0.0)
            var tax = intent.getDoubleExtra("tax",0.0)
            tax/=100;
            val imageUrl = extras.getString("image")
            val priceWithTax = price * (1 + tax)
            //represent price with tax
            binding.priceWithTax.text = applicationContext.getString(R.string.price_representation,price.toString(),tax.toString())
            //calculate price with tax
            binding.price.text = NumberFormat.getCurrencyInstance(Locale("en","in")).format(priceWithTax)
            //load image

            if(imageUrl!="")
                Picasso.get().load(imageUrl).into(binding.imagePreview);        //if product image exists load it using the link
            else
                binding.imagePreview.visibility=GONE

        }
    }
}