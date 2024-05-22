package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Product(
    var id: Long = 0,
    var name: String = "",
    var description: String = "",
    var producer: String = "",
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "name" to name,
            "description" to description,
            "producer" to producer,
        )
    }

}