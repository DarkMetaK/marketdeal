package br.com.marketdeal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    private val loginBtn by lazy { findViewById<Button>(R.id.activity_sign_in_login_btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_in)

        configLoginButton()
    }

    private fun configLoginButton() {
        loginBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

}