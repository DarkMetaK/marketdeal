package br.com.marketdeal.dto

data class MarketSpinnerDTO(
    var id: String,
    var name: String
) {

    override fun toString(): String {
        return name
    }

}
