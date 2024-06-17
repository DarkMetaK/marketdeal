package br.com.marketdeal.dto

data class ProductSpinnerDTO(
    var id: String,
    var name: String,
    var imageUrl: String?
) {

    override fun toString(): String {
        return name
    }

}
