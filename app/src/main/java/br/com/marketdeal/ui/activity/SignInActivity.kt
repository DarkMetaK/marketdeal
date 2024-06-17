package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private val auth by lazy { Firebase.auth }

    private val emailLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_in_email_layout) }
    private val email by lazy { findViewById<TextInputEditText>(R.id.activity_sign_in_email) }

    private val passwordLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_in_password_layout) }
    private val password by lazy { findViewById<TextInputEditText>(R.id.activity_sign_in_password) }

    private val loginBtn by lazy { findViewById<Button>(R.id.activity_sign_in_login_btn) }
    private val signUpBtn by lazy { findViewById<TextView>(R.id.activity_sign_in_redirect_link) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_in)

        configLoginButton()
        configSignUpLink()
    }

    private fun configLoginButton() {
        loginBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            if (validateFields()) {
                signIn()
            } else {
                showToast("Preencha todos os campos.")
            }

            it.isEnabled = true
            it.isClickable = true
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
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    showToast("Usuário não encontrado com essas credenciais.")
                }
            }
    }

    private fun validateFields(): Boolean {
        var amountOfErrors = 0
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        if (emailStr.isEmpty()) {
            emailLayout.error = "O e-mail é obrigatório."
            amountOfErrors++
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailLayout.error = "O e-mail não é válido."
            amountOfErrors++
        } else {
            emailLayout.error = null
        }

        if (passwordStr.isEmpty()) {
            passwordLayout.error = "A senha é obrigatória."
            amountOfErrors++
        } else {
            passwordLayout.error = null
        }

        return amountOfErrors == 0
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}