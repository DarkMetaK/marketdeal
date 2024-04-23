package br.com.marketdeal.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
    }

}