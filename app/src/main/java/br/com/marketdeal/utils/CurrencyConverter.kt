package br.com.marketdeal.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyConverter {

    fun convertToReal(value: Double): String {
        val locale = Locale("pt", "BR")

        return NumberFormat.getCurrencyInstance(locale).format(value)
    }

}