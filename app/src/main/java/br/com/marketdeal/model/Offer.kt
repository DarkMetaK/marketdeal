package br.com.marketdeal.model

import com.google.firebase.database.Exclude

data class Offer(
    val id: String = "",
    val size: String = "",
    val originalPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val observations: String = "",
    val marketId: String = "",
    val productId: String = "",
    val userId: String = "",
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to id,
            "size" to size,
            "originalPrice" to originalPrice,
            "currentPrice" to currentPrice,
            "observations" to observations,
            "marketId" to marketId,
            "productId" to productId,
            "userId" to userId,
        )
    }

}