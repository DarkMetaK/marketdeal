package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import br.com.marketdeal.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        val navView : BottomNavigationView = findViewById(R.id.activity_main_bn_navigation)
        val navController = findNavController(R.id.activity_main_container)

        navView.setupWithNavController(navController)
    }

}