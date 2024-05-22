package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class User(
    var id: String = "",
    var email: String = "",
    var name: String = "",
    var phone: String = "",
    var password: String = "",
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "email" to email,
            "name" to name,
            "phone" to phone,
            "password" to password,
        )
    }

}
