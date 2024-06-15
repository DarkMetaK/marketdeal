package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class MarketFormActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }

    private val registerBtn by lazy { findViewById<Button>(R.id.market_register_btn) }
    private val txtName by lazy { findViewById<EditText>(R.id.market_name) }
    private val txtStreet by lazy { findViewById<EditText>(R.id.street_market) }
    private val txtNumber by lazy { findViewById<EditText>(R.id.number_market) }
    private val txtNeighborhood by lazy { findViewById<EditText>(R.id.nbr_market) }
    private val txtCity by lazy { findViewById<EditText>(R.id.city_market) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_market)
        supportActionBar?.hide()
        configRegisterButton()
    }

    private fun configRegisterButton() {
        registerBtn.setOnClickListener {
            createMarket()
        }
    }

    private fun createMarket() {
        val nameStr = txtName.text.toString()
        val numberStr = txtNumber.text.toString()
        val streetStr = txtStreet.text.toString()
        val neighborhoodStr = txtNeighborhood.text.toString()
        val cityStr = txtCity.text.toString()

        if (nameStr.isBlank() || numberStr.isBlank() || streetStr.isBlank() || neighborhoodStr.isBlank() || cityStr.isBlank()) {
            Toast.makeText(
                baseContext,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val market = Market(
            id = UUID.randomUUID().toString(),
            name = nameStr,
            number = numberStr,
            street = streetStr,
            neighborhood = neighborhoodStr,
            city = cityStr
        )
        registerMarket(market)
    }

    private fun cleanFields() {
        txtStreet.setText("")
        txtName.setText("")
        txtNeighborhood.setText("")
        txtCity.setText("")
        txtNumber.setText("")
    }

    private fun registerMarket(market: Market) {
        database.child("markets").child(market.id).setValue(market)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Mercado registrado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
                cleanFields()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Falha ao registrar mercado, por favor tente novamente!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
