package br.com.marketdeal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R

class SignInActivity : AppCompatActivity() {

    private val loginBtn by lazy { findViewById<Button>(R.id.activity_sign_in_login_btn) }
    private val signUpBtn by lazy { findViewById<TextView>(R.id.activity_sign_in_redirect_link) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_in)

        configLoginButton()
        configSignUpButton()
    }

    private fun configLoginButton() {
        loginBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configSignUpButton() {
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}