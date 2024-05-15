package br.com.marketdeal.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import br.com.marketdeal.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoadingActivity : Activity() {
    private val auth by lazy { Firebase.auth }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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