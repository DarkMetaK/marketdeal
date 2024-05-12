package br.com.marketdeal.ui.activity

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import br.com.marketdeal.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoadingActivity : Activity(){
    private lateinit var sp: SharedPreferences
    private val auth by lazy { Firebase.auth }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loadingscreen)

        sp = getSharedPreferences("MarketDeal_crud", MODE_PRIVATE)
        previousSignIn()
    }
    private fun previousSignIn(){
        val sp = this.getSharedPreferences("MarketDeal_crud", MODE_PRIVATE)
        val emailStr = sp.getString("email","[]").toString()
        val passwordStr = sp.getString("password","[]").toString()

        auth.signInWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, SignInActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
    }
}