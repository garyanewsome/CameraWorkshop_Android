package com.teamtreehouse.cameraworkshop.cameraworkshop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val imageView = findViewById<View>(R.id.imageView) as ImageView

        val intent: Intent = getIntent()
        val imageUri: Uri? = intent.getData()
        Picasso.get().load(imageUri).into(imageView)


    }

    companion object {

        val TAG = ViewImageActivity::class.java.simpleName
    }
}