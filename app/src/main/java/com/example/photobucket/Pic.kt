package com.example.photobucket

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Pic(var caption: String = "", var url: String = "", val uid: String? = "") : Parcelable {

    @get:Exclude var id = ""
    @IgnoredOnParcel @ServerTimestamp var created: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "created"
        fun fromSnapshot(snapshot: DocumentSnapshot): Pic {
            val pic = snapshot.toObject(Pic::class.java)!!
            pic.id = snapshot.id
            return pic
        }
    }
}