package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private val email by lazy { findViewById<EditText>(R.id.activity_sign_up_email) }
    private val name by lazy { findViewById<EditText>(R.id.activity_sign_up_name) }
    private val phone by lazy { findViewById<EditText>(R.id.activity_sign_up_phone) }
    private val password by lazy { findViewById<TextView>(R.id.activity_sign_up_password) }
    private val registerBtn by lazy { findViewById<Button>(R.id.activity_sign_up_register_btn) }
    private val signInBtn by lazy { findViewById<TextView>(R.id.activity_sign_up_redirect_link) }

    private val auth by lazy { Firebase.auth }
    private val database by lazy { Firebase.database.reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)

        configRegisterButton()
        configSignInLink()
    }

    private fun configRegisterButton() {
        registerBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun configSignInLink() {
        signInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount() {
        val emailStr = email.text.toString()
        val nameStr = name.text.toString()
        val phoneStr = phone.text.toString()
        val passwordStr = password.text.toString()

        if (emailStr.isBlank() || passwordStr.isBlank() || phoneStr.isBlank() || passwordStr.isBlank()) {
            // Mostrar erros
            Toast.makeText(
                baseContext,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        registerUser(emailStr, passwordStr, nameStr, phoneStr)
    }

    private fun registerUser(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uuid = auth.currentUser!!.uid
                    val user = User(uuid, email, name, phone, password)
                    database.child("users").child(uuid).setValue(user)

                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Falha na criação do usuário.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

}