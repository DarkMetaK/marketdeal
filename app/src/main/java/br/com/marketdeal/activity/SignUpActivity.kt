package br.com.marketdeal.activity

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

    private val auth by lazy { Firebase.auth }
    private val database by lazy { Firebase.database.reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)

        configRegisterButton()
    }

    private fun configRegisterButton() {
        registerBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val emailStr = email.text.toString()
        var nameStr = name.text.toString()
        var phoneStr = phone.text.toString()
        val passwordStr = password.text.toString()

        registerNewUser(emailStr, nameStr, phoneStr, passwordStr)
        registerAuthentication(emailStr, passwordStr)
    }

    private fun registerNewUser(email: String, name: String, phone: String, password: String) {
        val user = User(email, name, phone, password)

        database.child("users").child(name).setValue(user)
    }

    private fun registerAuthentication(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

}