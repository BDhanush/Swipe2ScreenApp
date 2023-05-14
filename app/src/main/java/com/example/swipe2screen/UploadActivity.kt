package com.example.swipe2screen

import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.swipe2screen.databinding.ActivityUploadBinding
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    val productTypes:ArrayList<String> = arrayListOf("OS","Operating System","Operating System1","more","less","next")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val adapter
                = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, productTypes)
        binding.typeInput.setAdapter(adapter)

        binding.typeInput.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
            {
                binding.typeInput.showDropDown();
            }else {
                binding.typeInput.setOnClickListener {
                    binding.typeInput.showDropDown();
                }
            }
        }
        binding.upload.setOnClickListener {
            if(CheckAllFields())
            {


                finish()
            }
        }

    }
    private fun CheckAllFields(): Boolean {
        if (binding.productNameInput.length() == 0) {
            binding.productNameInput.error = "This field is required"
            return false
        }
        if (binding.typeInput.length() == 0) {
            binding.typeInput.error = "This field is required"
            return false
        }
        if (!productTypes.contains(binding.typeInput.text.toString())){
            binding.typeInput.error = "Type doesn't exist"
            return false
        }
        if (binding.priceInput.text.toString().isEmpty()) {
            binding.priceInput.error = "This field is required"
            return false
        }
        if (binding.taxRateInput.text.toString().isEmpty()) {
            binding.taxRateInput.error = "This field is required"
            return false
        }
        // after all validation return true.
        return true
    }
}