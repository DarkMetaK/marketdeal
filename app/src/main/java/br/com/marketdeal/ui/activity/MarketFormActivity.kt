package br.com.marketdeal.ui.activity

import android.os.Bundle
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
import java.util.UUID

class MarketFormActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }

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
            createMarket()
        }
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

    private fun createMarket() {
        val nameStr = name.text.toString()
        val numberStr = number.text.toString()
        val streetStr = street.text.toString()
        val neighborhoodStr = neighborhood.text.toString()
        val cityStr = city.text.toString()

        if (nameStr.isBlank() || numberStr.isBlank() || streetStr.isBlank() || neighborhoodStr.isBlank() || cityStr.isBlank()) {
            showToast("Preencha todos os campos")
            return
        }

        val market = Market(
            UUID.randomUUID().toString(),
            nameStr,
            cityStr,
            neighborhoodStr,
            streetStr,
            numberStr,
        )

        registerMarket(market)
    }

    private fun cleanFields() {
        cep.setText("")
        street.setText("")
        // name.setText("")
        neighborhood.setText("")
        city.setText("")
        // number.setText("")
    }

    private fun registerMarket(market: Market) {
        database.child("markets").child(market.uid).setValue(market)
            .addOnSuccessListener {
                showToast("Mercado registrado com sucesso!")
                cleanFields()

                finish()
            }
            .addOnFailureListener {
                showToast("Falha ao registrar mercado, por favor tente novamente!")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
