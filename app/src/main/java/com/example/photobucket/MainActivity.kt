package com.example.photobucket

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_edit.*
import kotlinx.android.synthetic.main.title_edit.view.*

private val SETTINGS = "settings"

class MainActivity : AppCompatActivity(), PicListFragment.OnPicSelectedListener {

    lateinit var adapter: PicAdapter
    val settingsRef = FirebaseFirestore
        .getInstance()
        .collection(SETTINGS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val fragment = PicListFragment(this)
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragment_container, fragment)
        ft.commit()

        settingsRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }
            for(docChange in querySnapshot!!.documentChanges) {
                val s = SettingsObject.fromSnapshot(docChange.document)
                toolbar.title = s.title
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                showTitleEditDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPicSelected(pic: Pic) {
        fab.hide()
        val picFragment = PicDetailFragment.newInstance(pic)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, picFragment)
        ft.addToBackStack("detail")
        ft.commit()
    }

    override fun onPicDetailExit() {
        fab.show()
    }

    fun showTitleEditDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.edit_title)
        val view = LayoutInflater.from(this).inflate(R.layout.title_edit, null, false)
        view.title_edit_text.setText(toolbar.title)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok) {_, _ ->
            val title = view.title_edit_text.text.toString()
            val s = SettingsObject(title)
            settingsRef.document(SETTINGS).set(s)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}
