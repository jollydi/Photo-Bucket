package com.example.photobucket

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val ARG_PIC = "pic"

class PicDetailFragment : Fragment() {

    private var pic: Pic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pic = it.getParcelable(ARG_PIC)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pic_detail, container, false)

        return view
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
}
