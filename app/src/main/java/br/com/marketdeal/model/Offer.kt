package br.com.marketdeal.model

import com.google.firebase.database.Exclude
import java.io.Serializable
import java.time.LocalDate

data class Offer(
    var uid: String = "",
    var size: String = "",
    var originalPrice: Double = 0.0,
    var currentPrice: Double = 0.0,
    var observations: String = "",
    var createdAt: String = LocalDate.now().toString(),
    var marketId: String = "",
    var marketName: String = "",
    var productId: String = "",
    var productName: String = "",
    var imageUrl: String? = "",
    var userId: String = "",
) : Serializable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "size" to size,
            "originalPrice" to originalPrice,
            "currentPrice" to currentPrice,
            "observations" to observations,
            "createdAt" to createdAt,
            "marketId" to marketId,
            "marketName" to marketName,
            "productId" to productId,
            "productName" to productName,
            "imageUrl" to imageUrl,
            "userId" to userId,
        )
    }

}