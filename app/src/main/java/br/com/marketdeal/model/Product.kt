package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Product(
    var uid: String = "",
    var name: String = "",
    var description: String = "",
    var producer: String = "",
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "description" to description,
            "producer" to producer,
        )
    }

}