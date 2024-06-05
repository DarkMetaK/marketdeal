package br.com.marketdeal.model

import com.google.firebase.database.Exclude
import java.time.LocalDate

data class Offer(
    val id: String = "",
    val size: String = "",
    val originalPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val observations: String = "",
    val createdAt: String = LocalDate.now().toString(),
    val marketId: String = "",
    val marketName: String = "",
    val productId: String = "",
    val productName: String = "",
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
            "createdAt" to createdAt,
            "marketId" to marketId,
            "marketName" to marketName,
            "productId" to productId,
            "productName" to productName,
            "userId" to userId,
        )
    }

}