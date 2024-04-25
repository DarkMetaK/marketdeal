package br.com.marketdeal.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private val email by lazy { findViewById<EditText>(R.id.activity_sign_in_email) }
    private val password by lazy { findViewById<TextView>(R.id.activity_sign_in_password) }
    private val loginBtn by lazy { findViewById<Button>(R.id.activity_sign_in_login_btn) }
    private val signUpBtn by lazy { findViewById<TextView>(R.id.activity_sign_in_redirect_link) }

    private val auth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_in)

        configLoginButton()
        configSignUpLink()
    }

    private fun configLoginButton() {
        loginBtn.setOnClickListener {
            signIn()
        }
    }

    private fun configSignUpLink() {
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        auth.signInWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticado com sucesso, redirecionar para home
                    Log.d(TAG, "signInWithEmail:success")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // Erro ao fazer login
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

}