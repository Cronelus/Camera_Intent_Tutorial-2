package com.android.cameraintenttutorial

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 0
    lateinit var imageFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraButton.setOnClickListener {
            try {
                val imageFile = createImageFile()
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if(callCameraIntent.resolveActivity(packageManager) != null){
                    val authorities = packageName + ".fileprovider"
                    val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)
                    callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(callCameraIntent,CAMERA_REQUEST_CODE)
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Could not create file!", Toast.LENGTH_SHORT).show()
            }
        }
        rgbValuesButton.setOnClickListener {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
/*                if (resultCode == Activity.RESULT_OK && data != null)
                    photoImageVIew.setImageBitmap(data.extras.get("data")as Bitmap)
            }*/
                if (resultCode == Activity.RESULT_OK)
                    photoImageVIew.setImageBitmap(setScaledBitmap())
                Toast.makeText(this, "Saved at " + imageFilePath, Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Unreconized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMDD_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timestamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDir.exists()) storageDir.mkdirs()
        val imageFile = createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
    }

    fun setScaledBitmap(): Bitmap {
        val imageViewWidth = photoImageVIew.width
        val imageViewHeight = photoImageVIew.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight

        val scaleFactor = Math.min(bitmapWidth /imageViewWidth, bitmapHeight /imageViewHeight)
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(imageFilePath, bmOptions)
    }
}
