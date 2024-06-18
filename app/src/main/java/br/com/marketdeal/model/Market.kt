package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Market(
    var uid: String = "",
    var imageUrl: String? = "",
    var name: String = "",
    var city: String = "",
    var neighborhood: String = "",
    var street: String = "",
    var number: String = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "city" to city,
            "neighborhood" to neighborhood,
            "street" to street,
            "imageUrl" to imageUrl,
            "number" to number,
        )
    }

}
