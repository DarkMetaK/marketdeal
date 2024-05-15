package br.com.marketdeal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var updateBtn: Button
    private lateinit var deleteBtn: Button

    private val userId by lazy { Firebase.auth.currentUser?.uid ?: null }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.activity_profile, container, false)
        initializeFields(view)
        configUpdateBtn()

        userTableRef?.addValueEventListener(userListener)
        return view
    }

    private fun initializeFields(view: View) {
        name = view.findViewById(R.id.activity_profile_name)
        phone = view.findViewById(R.id.activity_profile_phone)
        email = view.findViewById(R.id.activity_profile_email)
        password = view.findViewById(R.id.activity_profile_password)
        updateBtn = view.findViewById(R.id.activity_profile_update_btn)
        deleteBtn = view.findViewById(R.id.activity_profile_delete_btn)
    }

    private fun configUpdateBtn() {
        updateBtn.setOnClickListener {
            Log.i("Testando se clicou", "configUpdateBtn: |Teste")
            val user = createUser()
            updateUser(user)
        }
    }

    private fun createUser(): User {
        val nameStr = name.text.toString()
        val phoneStr = phone.text.toString()
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        return User(userId!!, emailStr, nameStr, phoneStr, passwordStr)
    }

    private fun loadUserData(user: User) {
        name.setText(user.name)
        phone.setText(user.phone)
        email.setText(user.email)
        password.setText(user.password)
    }

    private fun updateUser(user: User) {
        val userValues = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/$userId" to userValues,
        )

        database.updateChildren(childUpdates).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    "Perfil atualizado com sucesso!",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Falha ao atualizar os dados, por favor tente novamente.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}
