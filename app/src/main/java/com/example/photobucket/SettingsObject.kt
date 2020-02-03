package com.example.photobucket

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsObject(var title: String = "") : Parcelable {

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): SettingsObject {
            val s = snapshot.toObject(SettingsObject::class.java)!!
            return s
        }
    }
}