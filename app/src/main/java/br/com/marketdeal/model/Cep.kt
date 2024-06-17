package br.com.marketdeal.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Cep {
    var cep: String? = null
    var logradouro: String? = null
    var bairro: String? = null
    var cidade: String? = null
    var uf: String? = null
    var ddd: String? = null

    suspend fun buscarCep(cep: String) {
        withContext(Dispatchers.IO) {
            val url = URL("https://viacep.com.br/ws/$cep/json/")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            try {
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val input = BufferedReader(InputStreamReader(conn.inputStream))
                    val jsonResponse = input.use { it.readText() }
                    parseJson(jsonResponse)
                    input.close()
                } else {
                    throw IOException("Erro ao consultar o CEP: HTTP error code $responseCode")
                }
            } finally {
                conn.disconnect()
            }
        }
    }

    private fun parseJson(jsonResponse: String) {
        val json = Json { ignoreUnknownKeys = true }
        val jsonObject = json.parseToJsonElement(jsonResponse).jsonObject

        cep = jsonObject["cep"]?.jsonPrimitive?.contentOrNull
        logradouro = jsonObject["logradouro"]?.jsonPrimitive?.contentOrNull
        bairro = jsonObject["bairro"]?.jsonPrimitive?.contentOrNull
        cidade = jsonObject["localidade"]?.jsonPrimitive?.contentOrNull
        uf = jsonObject["uf"]?.jsonPrimitive?.contentOrNull
        ddd = jsonObject["ddd"]?.jsonPrimitive?.contentOrNull
    }
}
