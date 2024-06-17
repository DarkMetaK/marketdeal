package br.com.marketdeal.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Product(
    var uid: String = "",
    var imageUrl: String? = "",
    var name: String = "",
    var description: String = "",
    var producer: String = "",
): Serializable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "imageUrl" to imageUrl,
            "name" to name,
            "description" to description,
            "producer" to producer,
        )
    }

}