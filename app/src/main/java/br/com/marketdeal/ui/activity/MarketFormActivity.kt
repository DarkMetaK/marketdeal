package br.com.marketdeal.ui.activity
import androidx.fragment.app.Fragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class MarketFormActivity : AppCompatActivity(){
    private val database by lazy { Firebase.database.reference }

    private val registerBtn by lazy { findViewById<Button>(R.id.market_register_btn) }
    private val txtName by lazy { findViewById<EditText>(R.id.market_name) }
    private val txtAddress by lazy { findViewById<EditText>(R.id.address_market) }
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
        val addressStr = txtAddress.text.toString()

        if (nameStr.isBlank() || addressStr.isBlank()) {
            Toast.makeText(
                baseContext,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        registerMarket(nameStr, addressStr)
    }
    private fun cleanFields() {
        txtAddress.setText("")
        txtName.setText("")
    }

    private fun registerMarket(nameStr: String, addressStr: String) {
        val market = Market(UUID.randomUUID().toString(), nameStr, addressStr)

        database.child("markets").child(market.id).setValue(market)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Mercado registrado com sucesso!",
                    Toast.LENGTH_SHORT,
                ).show()
                cleanFields()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Falha ao registrar mercado, por favor tente novamente!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
    }
