package br.com.marketdeal.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.model.User
import br.com.marketdeal.ui.activity.SignInActivity
import br.com.marketdeal.utils.MaskWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private val userId by lazy { Firebase.auth.currentUser?.uid }
    private val database by lazy { Firebase.database.getReference("users") }
    private val userTableRef by lazy { userId?.let { database.child(it) } }
    private val userListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val user = dataSnapshot.getValue<User>()

            if (user != null) {
                loadUserData(user)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
        }
    }

    private lateinit var nameLayout: TextInputLayout
    private lateinit var name: TextInputEditText

    private lateinit var phoneLayout: TextInputLayout
    private lateinit var phone: TextInputEditText

    private lateinit var emailLayout: TextInputLayout
    private lateinit var email: TextInputEditText

    private lateinit var passwordLayout: TextInputLayout
    private lateinit var password: TextInputEditText

    private lateinit var logoutBtn: Button
    private lateinit var updateBtn: Button
    private lateinit var deleteBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        initializeFields(view)
        configLogoutBtn()
        configUpdateBtn()
        configDeleteBtn()

        userTableRef.let {
            userTableRef?.addValueEventListener(userListener)
        }

        return view
    }

    override fun onPause() {
        super.onPause()

        resetErrors()
    }

    private fun initializeFields(view: View) {
        nameLayout = view.findViewById(R.id.fragment_profile_name_layout)
        name = view.findViewById(R.id.fragment_profile_name)

        phoneLayout = view.findViewById(R.id.fragment_profile_phone_layout)
        phone = view.findViewById(R.id.fragment_profile_phone)
        phone.addTextChangedListener(MaskWatcher("## #####-####"))

        emailLayout = view.findViewById(R.id.fragment_profile_email_layout)
        email = view.findViewById(R.id.fragment_profile_email)

        passwordLayout = view.findViewById(R.id.fragment_profile_password_layout)
        password = view.findViewById(R.id.fragment_profile_password)

        logoutBtn = view.findViewById(R.id.fragment_profile_logout_btn)
        updateBtn = view.findViewById(R.id.fragment_profile_update_btn)
        deleteBtn = view.findViewById(R.id.fragment_profile_delete_btn)
    }

    private fun configLogoutBtn() {
        logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun configUpdateBtn() {
        updateBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            if (validateFields()) {
                updateUser { _ ->
                    it.isEnabled = true
                    it.isClickable = true
                }
            } else {
                showToast("Preencha todos os campos obrigatórios.")
                it.isEnabled = true
                it.isClickable = true
            }
        }
    }

    private fun configDeleteBtn() {
        deleteBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            deleteDialog { _ ->
                it.isEnabled = true
                it.isClickable = true
            }
        }
    }

    private fun loadUserData(user: User) {
        name.setText(user.name)
        phone.setText(user.phone)
        email.setText(user.email)
        password.setText(user.password)
    }

    private fun updateUser(callback: (Boolean) -> Unit) {
        val user = createUserModel()
        val userValues = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/$userId" to userValues,
        )

        database.updateChildren(childUpdates)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showToast("Perfil atualizado com sucesso!")
                    callback(true)
                } else {
                    showToast("Falha ao atualizar os dados, por favor tente novamente.")
                    callback(false)
                }
            }
    }

    private fun createUserModel(): User {
        val nameStr = name.text.toString()
        val phoneStr = phone.text.toString()
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        return User(userId!!, emailStr, nameStr, phoneStr, passwordStr)
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

    private fun deleteUser(callback: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Perfil deletado com sucesso!")
                    database.child(userId.toString()).removeValue()
                    callback(true)
                } else {
                    showToast("Falha ao remover usuario.")
                    callback(false)
                }
            }
    }

    private fun deleteDialog(callback: (Boolean) -> Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Deseja realmente apagar sua conta?")
            .setTitle("Exclusão de conta")
            .setPositiveButton("Confirmar") { _, _ ->
                deleteUser { userWasSuccessfullyDeleted ->
                    if (userWasSuccessfullyDeleted) {
                        val intent = Intent(requireActivity(), SignInActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                callback(false)
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut().let {
            val intent = Intent(requireActivity(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun resetErrors() {
        nameLayout.error = null
        phoneLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
