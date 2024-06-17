package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Market(
    val id: String = "",
    val name: String = "",
    val  number : String="",
    val  street : String="",
    val  neighborhood : String="",
    val  city : String=""

) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "name" to name,
            "address" to address,
        )
    }

}
