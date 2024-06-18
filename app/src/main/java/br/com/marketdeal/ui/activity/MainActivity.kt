package br.com.marketdeal.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import br.com.marketdeal.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val authUser by lazy { Firebase.auth.currentUser }

    private val navController by lazy { findNavController(R.id.activity_main_container) }
    private val navView by lazy { findViewById<BottomNavigationView>(R.id.activity_main_bn_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        setupBottomNavigation()
        loadFragment()
    }

    private fun setupBottomNavigation() {
        navView.setupWithNavController(navController)

        if (authUser?.email != "admin@gmail.com") {
            navView.removeItem(R.id.mi_store)
            navView.removeItem(R.id.mi_product)
        }
    }

    private fun BottomNavigationView.removeItem(id: Int) {
        if (menu.findItem(id) != null) {
            menu.removeItem(id)
        }
    }

    private fun loadFragment() {
        val intentFragment = intent?.extras?.getString("fragmentToLoad")

        if (intentFragment != null) {
            when (intentFragment) {
                "offer" -> {
                    navView.selectedItemId = R.id.mi_offer
                    intent.removeExtra("fragmentToLoad")
                }
            }
        }
    }

}