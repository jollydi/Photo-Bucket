package com.example.photobucket

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_edit.*
import kotlinx.android.synthetic.main.title_edit.view.*

private val SETTINGS = "settings"

class MainActivity : AppCompatActivity(),
    PicListFragment.OnPicSelectedListener,
    SplashFragment.OnLoginButtonPressedListener {

    lateinit var adapter: PicAdapter
    val settingsRef = FirebaseFirestore
        .getInstance()
        .collection(SETTINGS)
    private val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val RC_SIGN_IN = 1
    private var showUserPics = true
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initializeListeners()

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

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private fun initializeListeners() {
        // to the MovieQuoteFragment if the user is logged in
        // and goes back to the Splash fragment otherwise.
        // See https://firebase.google.com/docs/auth/users#the_user_lifecycle
        authListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            Log.d(Constants.TAG, "In auth listener, user = $user")
            if (user != null) {
                switchToPicListFragment(showUserPics)
            } else {
                switchToSplashFragment()
            }
        }
    }

    private fun switchToPicListFragment(picFilter: Boolean) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, PicListFragment(this, auth, picFilter))
        ft.commit()
    }

    private fun switchToSplashFragment() {
        fab.hide()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun launchLoginUI() {
        // For details, see https://firebase.google.com/docs/auth/android/firebaseui#sign_in

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher_background)
            .build()

        // Create and launch sign-in intent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu
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
            R.id.action_logout -> {
                auth.signOut()
                true
            }
            R.id.visible_photos -> {
                showUserPics = !showUserPics
                if (auth.currentUser != null) {
                    if (menu.getItem(0).title == getString(R.string.all)) {
                        menu.getItem(0).setTitle(R.string.mine)
                    } else {
                        menu.getItem(0).setTitle(R.string.all)
                    }
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, PicListFragment(this, auth, showUserPics))
                    ft.commit()
                }
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

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }
}
