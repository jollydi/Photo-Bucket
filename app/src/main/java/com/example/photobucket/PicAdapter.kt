package com.example.photobucket

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.edit_dialog.view.*

private const val PIC_COLLECTION = "pics"

class PicAdapter(val listener: PicListFragment.OnPicSelectedListener?, val context: Context?) : RecyclerView.Adapter<PicViewHolder>() {

    private val pics = ArrayList<Pic>()

    private val picsRef = FirebaseFirestore
        .getInstance()
        .collection(PIC_COLLECTION)

    init {
        picsRef
            .orderBy(Pic.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "${snapshot?.documentChanges?.size}")
                for (docChange in snapshot!!.documentChanges) {
                    val pic = Pic.fromSnapshot(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            pics.add(0, pic)
                            notifyItemInserted(0)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val pos = pics.indexOfFirst { pic.id == it.id }
                            pics.removeAt(pos)
                            notifyItemRemoved(pos)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val pos = pics.indexOfFirst { pic.id == it.id }
                            pics[pos] = pic
                            notifyItemChanged(pos)
                        }
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
        Log.d(Constants.TAG, "Creating VH")
        val view = LayoutInflater.from(context).inflate(R.layout.pic_row_view, parent, false)
        return PicViewHolder(view, this, context)
    }

    override fun getItemCount(): Int {
        return pics.size
    }

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
        holder.bind(pics[position])
    }

    fun add(pic: Pic) {
        picsRef.add(pic)
    }

    private fun remove(index: Int) {
        picsRef.document(pics[index].id).delete()
    }

    private fun edit(position: Int, caption: String, url: String) {
        pics[position].caption = caption
        pics[position].url = url
        picsRef.document(pics[position].id).set(pics[position])
    }

    fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.edit_title)
        val view = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null, false)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val caption = view.caption_edit_text.text.toString()
            val url = view.url_edit_text.text.toString()
            edit(position, caption, url)
        }
    }

    fun selectPicAt(position: Int) {
        listener!!.onPicSelected(pics[position])
    }
}