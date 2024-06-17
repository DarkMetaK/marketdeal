package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.User
import br.com.marketdeal.utils.MaskWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private val auth by lazy { Firebase.auth }
    private val database by lazy { Firebase.database.reference }

    private val nameLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_up_name_layout) }
    private val name by lazy { findViewById<TextInputEditText>(R.id.activity_sign_up_name) }

    private val phoneLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_up_phone_layout) }
    private val phone by lazy { findViewById<TextInputEditText>(R.id.activity_sign_up_phone) }

    private val emailLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_up_email_layout) }
    private val email by lazy { findViewById<TextInputEditText>(R.id.activity_sign_up_email) }

    private val passwordLayout by lazy { findViewById<TextInputLayout>(R.id.activity_sign_up_password_layout) }
    private val password by lazy { findViewById<TextInputEditText>(R.id.activity_sign_up_password) }

    private val registerBtn by lazy { findViewById<Button>(R.id.activity_sign_up_register_btn) }
    private val signInBtn by lazy { findViewById<TextView>(R.id.activity_sign_up_redirect_link) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)

        phone.addTextChangedListener(MaskWatcher("## #####-####"))
        configRegisterButton()
        configSignInLink()
    }

    private fun configRegisterButton() {
        registerBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            if (validateFields()) {
                registerUser { userWasSuccessfullyRegistered ->
                    if (userWasSuccessfullyRegistered) {
                        val intent = Intent(this, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        showToast("Falha na criação do usuário.")
                        it.isEnabled = true
                        it.isClickable = true
                    }
                }
            } else {
                showToast("Preencha todos os campos obrigatórios.")
            }
        }
    }

    private fun configSignInLink() {
        signInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateFields(): Boolean {
        var amountOfErrors = 0
        val nameStr = name.text.toString()
        val phoneStr = phone.text.toString()
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        if (nameStr.isEmpty()) {
            nameLayout.error = "O nome é obrigatório."
            amountOfErrors++
        } else {
            nameLayout.error = null
        }

        if (phoneStr.isEmpty()) {
            phoneLayout.error = "O telefone é obrigatório."
            amountOfErrors++
        } else if (!phoneStr.matches(Regex("\\d{2} \\d{5}-\\d{4}"))) {
            phoneLayout.error = "O telefone deve estar no formato '99 99999-9999'."
            amountOfErrors++
        } else {
            phoneLayout.error = null
        }

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
        } else if (!passwordStr.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"))) {
            passwordLayout.error = "A senha deve ter no mínimo 8 caracteres, com pelo menos 1 letra e 1 número."
            amountOfErrors++
        } else {
            passwordLayout.error = null
        }

        return amountOfErrors == 0
    }

    private fun registerUser(callback: (Boolean) -> Unit) {
        val emailStr = email.text.toString()
        val nameStr = name.text.toString()
        val phoneStr = phone.text.toString()
        val passwordStr = password.text.toString()

        auth.createUserWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uuid = auth.currentUser!!.uid
                    val user = User(uuid, emailStr, nameStr, phoneStr, passwordStr)
                    database.child("users").child(uuid).setValue(user)
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}