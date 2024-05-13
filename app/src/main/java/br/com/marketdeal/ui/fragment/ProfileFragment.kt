package br.com.marketdeal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.activity_profile) {

    private val name by lazy { view?.findViewById<EditText>(R.id.activity_profile_name) }
    private val phone by lazy { view?.findViewById<EditText>(R.id.activity_profile_phone) }
    private val email by lazy { view?.findViewById<EditText>(R.id.activity_profile_email) }
    private val password by lazy { view?.findViewById<EditText>(R.id.activity_profile_password) }
    private val updateBtn by lazy { view?.findViewById<Button>(R.id.activity_profile_update_btn) }
    private val deleteBtn by lazy { view?.findViewById<Button>(R.id.activity_profile_delete_btn) }

    private val database by lazy { Firebase.database.reference }
    private val usersRef by lazy {
        Firebase.database.getReference("users").child("d7aaC0tZXScv4lqyGWlCRTldlUO2")
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usersRef.addValueEventListener(userListener)
    }

    private fun loadUserData(user: User) {
        name?.setText(user.name)
        phone?.setText(user.phone)
        email?.setText(user.email)
        password?.setText(user.password)
    }

}