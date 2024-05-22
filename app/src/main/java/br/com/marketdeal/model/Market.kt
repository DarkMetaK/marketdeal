package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Market(
    val id: Long = 0L,
    val name: String = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "name" to name,
        )
    }

}