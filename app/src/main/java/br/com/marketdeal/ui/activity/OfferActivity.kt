package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Offer
import br.com.marketdeal.model.Market
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class OfferActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }
    private val userId by lazy { Firebase.auth.currentUser?.uid ?: null }
    
    private val title by lazy { findViewById<TextView>(R.id.activity_offer_title) }
    private val date by lazy { findViewById<TextView>(R.id.activity_offer_date) }
    private val size by lazy { findViewById<TextView>(R.id.activity_offer_size) }
    private val originalPrice by lazy { findViewById<TextView>(R.id.activity_offer_original_price) }
    private val currentPrice by lazy { findViewById<TextView>(R.id.activity_offer_current_price) }
    private val observations by lazy { findViewById<TextView>(R.id.activity_offer_observations) }

    private val marketName by lazy { findViewById<TextView>(R.id.activity_offer_market_name) }
    private val marketAddress by lazy { findViewById<TextView>(R.id.activity_offer_market_address) }

    private val deleteBtn by lazy { findViewById<Button>(R.id.activity_offer_delete_btn) }

    private lateinit var offer: Offer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_offer)

        retrieveIdAndFetchOfferData()
        configDeleteButton()
    }

    private fun retrieveIdAndFetchOfferData() {
        val intent = intent

        if (intent.hasExtra("id")) {
            val id = intent.getSerializableExtra("id") as String

            database.child("offers").child(id).get().addOnSuccessListener {
                val foundOffer = it.getValue(Offer::class.java)

                if (foundOffer != null) {
                    offer = foundOffer
                    initializeFields()
                    retrieveMarketData()
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting offer data", it)
            }
        } else {
            finish()
        }
    }

    private fun retrieveMarketData() {
        database.child("markets").child(offer.marketId).get().addOnSuccessListener {
            val market = it.getValue(Market::class.java)

            if (foundMarket != null) {
                marketName.text = market.name
                marketAddress.text = market.address
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting market data", it)
        }
    }

    private fun initializeFields() {
        title.text = offer.productName
        date.text = offer.createdAt
        size.text = offer.size
        originalPrice.text = offer.originalPrice.toString()
        currentPrice.text = offer.currentPrice.toString()
        observations.text = offer.observations
    }

     private fun deleteOffer() {
        database.child("offers").child(offer.id).removeValue()
        Toast.makeText(
            baseContext,
            "Oferta deletada com sucesso!",
            Toast.LENGTH_SHORT,
        ).show()

        finish()
    }

     private fun configDeleteButton() {
         deleteBtn.setOnClickListener {
            if(userId != null && offer.userId.equals(userId)) {
                deleteOffer()
            }
            else {
                Toast.makeText(
                    baseContext,
                    "Você não possui permissão para deletar esta oferta!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
     }

}
