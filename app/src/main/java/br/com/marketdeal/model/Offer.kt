package br.com.marketdeal.model

import com.google.firebase.database.Exclude
import java.math.BigDecimal
import java.util.UUID

data class Offer(
    val id: UUID = UUID.randomUUID(),
    val size: String = "",
    val originalPrice: BigDecimal = BigDecimal.ZERO,
    val currentPrice: BigDecimal = BigDecimal.ZERO,
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