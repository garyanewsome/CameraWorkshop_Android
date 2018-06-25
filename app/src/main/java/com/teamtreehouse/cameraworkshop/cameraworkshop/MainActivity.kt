package com.teamtreehouse.cameraworkshop.cameraworkshop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.getSimpleName()

    private var mMediaUri: Uri? = null

    private var takePhotoImage: ImageView? = null
    private var takeVideoImage: ImageView? = null
    private var pickPhotoImage: ImageView? = null
    private var pickVideoImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?)  {
        val builder = StrictMode.VmPolicy.Builder().build()
        StrictMode.setVmPolicy(builder)

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        takePhotoImage = findViewById(R.id.takePhoto)
        takeVideoImage = findViewById(R.id.takeVideo)
        pickPhotoImage = findViewById(R.id.pickPhoto)
        pickVideoImage = findViewById(R.id.pickVideo)

        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    mMediaUri = data.data
                }

                val intent = Intent(this, ViewImageActivity::class.java)
                intent.setData(mMediaUri)
                startActivity(intent)
            } else if (requestCode == REQUEST_TAKE_VIDEO){
                val intent = Intent(Intent.ACTION_VIEW, mMediaUri)
                intent.setDataAndType(mMediaUri, "video/*")
                startActivity(intent)
            } else if (resultCode == REQUEST_PICK_VIDEO) {
                if (data != null) {
                    Log.i(TAG, "Video content URI: " + data.data)
                    Toast.makeText(this, "Video content URI: " + data.getData(),
                            Toast.LENGTH_LONG).show()
                }
            }


        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show()
        }
    }

    fun setListeners() {
        takePhotoImage!!.setOnClickListener {
            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
            if (mMediaUri == null) {
                Toast.makeText(this,
                        "There was a problem accessing your device's external storage.",
                        Toast.LENGTH_LONG).show()
            } else {
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri)
                startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO)
            }

        }

        takeVideoImage!!.setOnClickListener {
            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO)
            if (mMediaUri == null) {
                Toast.makeText(this,
                        "There was a problem accessing your device's external storage.",
                        Toast.LENGTH_LONG).show()
            } else {
                val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri)
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO)
            }
        }

        pickPhotoImage!!.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_PHOTO)
        }

        pickVideoImage!!.setOnClickListener {
            val pickVideoIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickVideoIntent.type = "video/*"
            startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO)
        }
    }

    private fun getOutputMediaFileUri(mediaType: Int): Uri? {
        if (isExternalStorageAvailable()) {
            val mediaStorageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            var fileName: String? = null
            var fileType: String? = null
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

            if (mediaType == MEDIA_TYPE_IMAGE){
                fileName = "IMG_"+ timeStamp
                fileType = ".jpg"
            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                fileName = "VID_"+ timeStamp
                fileType = ".mp4"
            } else {
                return null
            }

            try {
                val mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir)
                Log.i(TAG, "File: " + Uri.fromFile(mediaFile))

                // 4. Return the file's URI
                return Uri.fromFile(mediaFile)
            }
            catch (e: IOException) {
                Log.e(TAG, "Error creating file: " +
                        mediaStorageDir.getAbsolutePath() + fileName + fileType)
            }
        }

        // something went wrong
        return null
    }

    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true
        }
        return false
    }

    companion object {
        val REQUEST_TAKE_PHOTO = 0
        val REQUEST_TAKE_VIDEO = 1
        val REQUEST_PICK_PHOTO = 2
        val REQUEST_PICK_VIDEO = 3
        val MEDIA_TYPE_IMAGE = 4
        val MEDIA_TYPE_VIDEO = 5
    }
}
