package com.example.swipe2screen

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swipe2screen.databinding.ActivityUploadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val productTypes:ArrayList<String> = arrayListOf("OS","Service","MNC","Other","pen")        //array of selectable product types
    private val SELECT_PICTURE = 200;
    private var selectedImageUri:Uri?=null;             //for a selectedImageUri

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
                binding.typeInput.showDropDown();       //to display the selectable list of product types when the field gains focus
            }
        }
        binding.typeInput.setOnClickListener {
            binding.typeInput.showDropDown();           //to display the selectable list of product types on a click
        }

        binding.addImage.setOnClickListener{
            chooseImage()                               //select an image from the device storage

        }

        binding.upload.setOnClickListener {
            if(checkAllFields())                        //form validation
            {
                lockUploadButton()                      //lock the upload button
                upload()                                //initiate post to api endpoint

            }
        }

    }

    private fun lockUploadButton()
    {
        binding.upload.isEnabled=false      //disable button
        binding.upload.text="Uploading"     //and change text to Uploading
    }
    private fun unlockUploadButton()
    {
        binding.upload.isEnabled=true       //enable button
        binding.upload.text="Upload"        //and change text to Upload
    }
    private fun upload(){

        val retrofitBuffer= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(dataURL).build().create(ApiInterface::class.java)


        val filesDir=applicationContext.filesDir
        val file=File(filesDir,"product_image.png")     //create a tempfile for image
        val outputStream=FileOutputStream(file)

        //add image via ImageView (1:1 ratio)
        val imageDrawable=binding.imagePreview.drawable as BitmapDrawable?
        if(imageDrawable!=null) {                           //if image is selected, get a bitmap of it and put it in tempfile
            val bitmap = imageDrawable.bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

//        //add Image via Uri (no crop)
//        val inputStream= selectedImageUri?.let { contentResolver.openInputStream(it) }    //get input stream from selected image's uri
//        inputStream?.copyTo(outputStream)                                                 //put it in tempfile

        val requestBody = RequestBody.create(MediaType.parse("image/*"),file)         //request body for file
        val filePart:MultipartBody.Part? = if(selectedImageUri!=null)                       //preparing a MultipartBody.Part for uploading the selected image file
                MultipartBody.Part.createFormData("files[]", file.name,requestBody)
            else
                null

        //prepare all required parts (parameters for api endpoint)
        val pricePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.priceInput.text.toString())
        val taxPart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.taxRateInput.text.toString())
        val productNamePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.productNameInput.text.toString())
        val productTypePart = RequestBody.create(MediaType.parse("multipart/form-data"),binding.typeInput.text.toString())

        //call the api endpoint (post/add) with parameter values
        val retrofitData = retrofitBuffer.upload(pricePart,productNamePart,productTypePart,taxPart,filePart)

        //check response and act accordingly
        retrofitData.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val code=response.code()
//                Toast.makeText(applicationContext,"$code",Toast.LENGTH_SHORT).show()
                Log.e(TAG,response.toString())
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext,"Product added", Toast.LENGTH_SHORT).show()
                    finish()                    //close the activity if success
                    Intent(baseContext, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}, ${response.message()}")
                    Toast.makeText(applicationContext, "Error adding product", Toast.LENGTH_SHORT).show()
                    unlockUploadButton()        //unlock the upload button
                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext,t.message?:"Error", Toast.LENGTH_SHORT).show()
                unlockUploadButton()            //unlock the upload button
            }
        })

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
            selectedImageUri = data!!.data          //Uri of the selected image from storage

            //change information of screen based on if an image is selected
            if(selectedImageUri!=null)
            {
                binding.addImage.text="Choose another image"
                binding.addImage.icon= ContextCompat.getDrawable(this, R.drawable.baseline_add_24)
                binding.imagePreview.setImageURI(selectedImageUri)
            }else{
                binding.addImage.text="Add Image"
                binding.addImage.icon= ContextCompat.getDrawable(this,R.drawable.baseline_upload_24)
                binding.imagePreview.setImageDrawable(null)
            }

        }
    }

    private fun checkAllFields(): Boolean {
        var check:Boolean=true;
        if (binding.productNameInput.length() == 0) {
            binding.productNameInput.error = "This field is required"
            check = false
        }
        if (binding.typeInput.text.toString().isEmpty()) {
            binding.typeInput.error = "This field is required"
            check = false
        }else if (!productTypes.contains(binding.typeInput.text.toString())){
            binding.typeInput.error = "Type doesn't exist"
            check = false
        }else{
            binding.typeInput.error=null
        }
        if (binding.priceInput.length() == 0) {
            binding.priceInput.error = "This field is required"
            check = false
        }
        if (binding.priceInput.text.toString()==".") {
            binding.priceInput.error = "Enter a decimal"
            check = false
        }
        if (binding.taxRateInput.length() == 0) {
            binding.taxRateInput.error = "This field is required"
            check = false
        }
        if (binding.taxRateInput.text.toString()==".") {
            binding.taxRateInput.error = "Enter a decimal"
            check = false
        }
        // after all validation return true.
        return check
    }

}