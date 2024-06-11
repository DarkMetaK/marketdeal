package br.com.marketdeal.model

import android.location.Address
import com.google.firebase.database.Exclude

data class Market(
    val id: String = "",
    val name: String = "",
    val  address : String=""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "name" to name,
        )
    }

}