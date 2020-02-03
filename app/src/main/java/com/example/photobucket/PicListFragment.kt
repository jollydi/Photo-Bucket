package com.example.photobucket

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

private const val ARG_LIST = "list"

class PicListFragment(val context: MainActivity) : Fragment() {
    private var listener: OnPicSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_pic_list, container, false) as RecyclerView
        val adapter = PicAdapter(listener, context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        listener?.onPicDetailExit()
        context.fab.setOnClickListener {
            adapter.showAddDialog()
        }
        val callback = SwipeCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        return recyclerView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPicSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnPicSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnPicSelectedListener {
        fun onPicSelected(pic: Pic)
        fun onPicDetailExit()
    }
}
