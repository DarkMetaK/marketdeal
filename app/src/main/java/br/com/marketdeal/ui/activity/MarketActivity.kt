package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MarketActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }

    private val name by lazy { findViewById<TextView>(R.id.market_name) }
    private val street by lazy { findViewById<TextView>(R.id.activity_market_street) }
    private val number by lazy { findViewById<TextView>(R.id.activity_market_street_number) }
    private val neighborhood by lazy { findViewById<TextView>(R.id.activity_market_neighborhood) }
    private val city by lazy { findViewById<TextView>(R.id.activity_market_city) }
    private  val deleteBtn by lazy { findViewById<Button>(R.id.activity_market_delete_btn) }
    private lateinit var market: Market

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_market)

        retrieveIdAndFetchMarketData()
    }

    private fun retrieveIdAndFetchMarketData() {
        if (intent.hasExtra("id")) {
            val id = intent.getSerializableExtra("id") as String

            database.child("markets").child(id).get().addOnSuccessListener {
                val foundMarket = it.getValue(Market::class.java)

                if (foundMarket != null) {
                    market = foundMarket

                    initializeFields()
                    configDeleteButton()
                    //configEditButton()
                    //retrieveMarketData()
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting offer data", it)
            }
        } else {
            finish()
        }
    }
/*
    private fun retrieveMarketData() {
        database.child("markets").child(market.id).get().addOnSuccessListener {
            val market = it.getValue(Market::class.java)

            if (market != null) {
                marketName.text = market.name
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting market data", it)
        }
    }*/

    private fun initializeFields() {
        name.text = market.name
        street.text = market.street
        number.text = market.number
        neighborhood.text = market.neighborhood
        city.text = market.city
    }

    private fun deleteMarket() {
        database.child("markets").child(market.id).removeValue()
        Toast.makeText(
            this,
            "Oferta deletada com sucesso!",
            Toast.LENGTH_SHORT,
        ).show()

        finish()
    }

    private fun configDeleteButton() {
            deleteBtn.setOnClickListener{
                deleteMarket()
        }
    }
    /*
    private fun configEditButton() {
        if (userId != null) {
            editBtn.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragmentToLoad", "market")
                intent.putExtra("market", market)

                startActivity(intent)
            }
        } else {
            editBtn.visibility = View.GONE
        }
    }*/

}
