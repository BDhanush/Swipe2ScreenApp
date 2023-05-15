package com.example.swipe2screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swipe2screen.databinding.ActivityUploadBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.*


class UploadActivity : AppCompatActivity() {
    var storage = Firebase.storage

    private lateinit var binding: ActivityUploadBinding
    val postURL="https://app.getswipe.in/api/public/"
    private val productTypes:ArrayList<String> = arrayListOf("OS","Service","MNC","Other","pen")
    private val SELECT_PICTURE = 200;
    private var selectedImageUri:Uri?=null;

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
        binding.addImage.setOnClickListener{
            chooseImage()

        }

        binding.upload.setOnClickListener {
            if(checkAllFields())
            {
                postProductInfo()

            }
        }

    }

    private fun postProductInfo()
    {
        var imagePath:Uri?=null
        if(selectedImageUri!=null) {

            val storageRef = storage.reference.child("/images/${System.currentTimeMillis()}")
            Toast.makeText(baseContext,selectedImageUri.toString(),Toast.LENGTH_SHORT).show()
            val uploadTask=storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    //get url of uploaded video
                    Toast.makeText(baseContext,taskSnapshot.storage.downloadUrl.toString(),Toast.LENGTH_SHORT).show()

                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while(!uriTask.isSuccessful);
                    imagePath = uriTask.result
                }
        }
        val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),imagePath.toString())
        val pricePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.priceInput.text.toString())
        val taxPart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.taxRateInput.text.toString())
        val productNamePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.productNameInput.text.toString())
        val productTypePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.typeInput.text.toString())

        val retrofitBuffer= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(dataURL).build().create(ApiInterface::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response:ResponseBody = retrofitBuffer.postProductInfo(imagePart,pricePart,productNamePart,productTypePart,taxPart)
//            Toast.makeText(baseContext,response.toString(),Toast.LENGTH_SHORT).show()
            finish()
            Intent(baseContext, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

    }

    private fun chooseImage()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode==SELECT_PICTURE)
        {
            selectedImageUri = data!!.data

            if(selectedImageUri!=null)
            {
                binding.addImage.text="Choose another image"
                binding.addImage.icon= ContextCompat.getDrawable(this, R.drawable.baseline_add_24)
            }else{
                binding.addImage.text="Add Image"
                binding.addImage.icon= ContextCompat.getDrawable(this,R.drawable.baseline_upload_24)
            }

        }
    }

    private fun checkAllFields(): Boolean {
        var check:Boolean=true;
        if (binding.productNameInput.length() == 0) {
            binding.productNameInput.error = "This field is required"
            check = false
        }
        if (binding.typeInput.length() == 0) {
            binding.typeInput.error = "This field is required"
            check = false
        }else if (!productTypes.contains(binding.typeInput.text.toString())){
            binding.typeInput.error = "Type doesn't exist"
            check = false
        }
        if (binding.priceInput.text.toString().isEmpty()) {
            binding.priceInput.error = "This field is required"
            check = false
        }
        if (binding.taxRateInput.text.toString().isEmpty()) {
            binding.taxRateInput.error = "This field is required"
            check = false
        }
        // after all validation return true.
        return check
    }

}