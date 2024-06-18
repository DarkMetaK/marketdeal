package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.marketdeal.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoadingActivity : AppCompatActivity() {
    private val auth by lazy { Firebase.auth }
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_loading)

        previousSignIn()
    }

    private fun previousSignIn() {
        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

}