package com.example.photobucket

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.pic_row_view.view.*

class PicViewHolder : RecyclerView.ViewHolder {

    lateinit var captionTV: TextView
    lateinit var urlTV: TextView
    var context: Context?

    constructor(view: View, adapter: PicAdapter, context: Context?) : super(view) {
        captionTV = view.pic_caption
        urlTV = view.pic_url
        view.setOnClickListener {
            adapter.selectPicAt(adapterPosition)
            true
        }
        view.setOnLongClickListener {
            adapter.showEditDialog(adapterPosition)
            true
        }
        this.context = context
    }

    fun bind(pic: Pic) {
        captionTV.text = pic.caption
        urlTV.text = pic.url
    }
}