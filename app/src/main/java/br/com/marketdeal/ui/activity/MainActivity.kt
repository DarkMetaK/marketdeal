package br.com.marketdeal.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import br.com.marketdeal.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.activity_main_container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        setupBottomNavigation()
        loadFragment()
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.activity_main_bn_navigation)

        navView.setupWithNavController(navController)
    }

    private fun loadFragment() {
        val intentFragment = intent?.extras?.getString("fragmentToLoad")
        when (intentFragment) {
            "offer" -> {
                navController.navigate(R.id.mi_offer)
            }
        }
    }

}