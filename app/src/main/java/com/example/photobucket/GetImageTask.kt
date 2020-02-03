package com.example.photobucket

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.lang.Exception
import java.net.URL

class GetImageTask(private var imageConsumer: ImageConsumer): AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg params: String?): Bitmap? {
        val url = URL(params[0])
        return try {
            val inStream = url.openStream()
            val bitmap = BitmapFactory.decodeStream(inStream)
            bitmap
        } catch (e: Exception) {
            Log.e(Constants.TAG, "EXCEPTION: " + e.toString())
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        imageConsumer.onImageLoaded(result)
    }

    interface ImageConsumer {
        fun onImageLoaded(bitmap: Bitmap?)
    }
}