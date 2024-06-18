package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.utils.CepSearcher
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MarketUpdateActivity: AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }
    private lateinit var market: Market
    private val name by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_name) }
    private val cep by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_cep) }
    private val street by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_street) }
    private val number by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_number) }
    private val neighborhood by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_nbr) }
    private val city by lazy { findViewById<TextInputEditText>(R.id.activity_form_market_city) }
    private val registerBtn by lazy { findViewById<Button>(R.id.activity_form_market_register_btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_market)
        supportActionBar?.hide()
        registerBtn.setText("Alterar estabelecimento")

        retrieveIdAndFetchMarketData()
        configInputs()
    }

    private fun configInputs() {
        cep.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (cep.text.toString().length == 8) {
                    buscarCep(cep.text.toString())
                } else {
                    cleanFields()
                }
            }
        }

        registerBtn.setOnClickListener {
            updateMarket()
        }
    }

    private fun retrieveIdAndFetchMarketData() {
        if (intent.hasExtra("id")) {
            val id = intent.getSerializableExtra("id") as String

            database.child("markets").child(id).get().addOnSuccessListener {
                val foundMarket = it.getValue(Market::class.java)

                if (foundMarket != null) {
                    market = foundMarket
                    populateFields()
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting market data", it)
                showToast("Erro ao carregar dados do mercado")
                finish()
            }
        } else {
            showToast("ID do mercado não fornecido")
            finish()
        }
    }

    private fun populateFields() {
        name.setText(market.name)
        street.setText(market.street)
        number.setText(market.number)
        neighborhood.setText(market.neighborhood)
        city.setText(market.city)
        // Add code to set cep if you have it in the Market model
    }

    private fun buscarCep(numCep: String) {
        val searcher = CepSearcher()

        lifecycleScope.launch {
            try {
                searcher.buscarCep(numCep)

                if (searcher.cidade != null) {
                    street.setText(searcher.logradouro)
                    neighborhood.setText(searcher.bairro)
                    city.setText(searcher.cidade)

                    showToast("Endereço encontrado!")
                } else {
                    showToast("Endereço não encontrado, verifique o CEP e digite novamente.")
                    cleanFields()
                }
            } catch (e: Exception) {
                showToast("Erro ao buscar endereço, por favor tente novamente.")
                cleanFields()
                e.printStackTrace()
            }
        }
    }

    private fun updateMarket() {
        val nameStr = name.text.toString()
        val numberStr = number.text.toString()
        val streetStr = street.text.toString()
        val neighborhoodStr = neighborhood.text.toString()
        val cityStr = city.text.toString()

        if (nameStr.isBlank() || numberStr.isBlank() || streetStr.isBlank() || neighborhoodStr.isBlank() || cityStr.isBlank()) {
            showToast("Preencha todos os campos")
            return
        }

        val updatedMarket = market.copy(
            name = nameStr,
            city = cityStr,
            neighborhood = neighborhoodStr,
            street = streetStr,
            number = numberStr,
        )

        database.child("markets").child(updatedMarket.uid).setValue(updatedMarket)
            .addOnSuccessListener {
                showToast("Mercado atualizado com sucesso!")
                val intent = Intent(this, MarketActivity ::class.java)
                intent.putExtra("id", market.uid)
                startActivity(intent)
            }
            .addOnFailureListener {
                showToast("Falha ao atualizar mercado, por favor tente novamente!")
            }
    }

    private fun cleanFields() {
        street.setText("")
        neighborhood.setText("")
        city.setText("")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
