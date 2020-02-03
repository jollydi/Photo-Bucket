package com.example.photobucket

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_pic_detail.view.*

private const val ARG_PIC = "pic"

class PicDetailFragment : Fragment(), GetImageTask.ImageConsumer {

    private var pic: Pic? = null
    private var bitmap: Bitmap? = null
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pic = it.getParcelable(ARG_PIC)
        }
        GetImageTask(this).execute(pic?.url)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pic_detail, container, false)
        rootView.pic_caption.text = pic?.caption
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(pic: Pic) =
            PicDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIC, pic)
                }
            }
    }

    override fun onImageLoaded(bitmap: Bitmap?) {
        this.bitmap = bitmap
        rootView.pic_image.setImageBitmap(bitmap)
    }
}
