package br.com.marketdeal.activity

import android.content.Intent
import android.os.Bundle
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

        if (emailStr.isNullOrBlank() || passwordStr.isNullOrBlank()) {
            // Mostrar erros
            Toast.makeText(
                baseContext,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        auth.signInWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Usuário não encontrado com este e-mail e senha.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

}