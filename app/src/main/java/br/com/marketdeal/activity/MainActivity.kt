package br.com.marketdeal.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val auth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /*override fun onStart() {
        super.onStart()
        redirectIfAuthenticated()
    }

    // Era pra redirecionar caso o usuário esteja logado
    // Mas desconfio que ele roda antes do firebase retornar o resultado
    // Pendente

    private fun redirectIfAuthenticated() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }*/

}