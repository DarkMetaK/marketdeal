package br.com.marketdeal.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Product(
    var uid: String = "",
    var imageUrl: String? = "",
    var name: String = "",
    var producer: String = "",
    var description: String = "",
): Serializable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "imageUrl" to imageUrl,
            "name" to name,
            "producer" to producer,
            "description" to description,
        )
    }

}