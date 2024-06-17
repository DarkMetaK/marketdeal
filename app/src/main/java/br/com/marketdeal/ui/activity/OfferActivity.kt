package br.com.marketdeal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.model.Offer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
    private val editBtn by lazy { findViewById<Button>(R.id.activity_offer_edit_btn) }

    private lateinit var offer: Offer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_offer)

        retrieveIdAndFetchOfferData()
    }

    private fun retrieveIdAndFetchOfferData() {
        if (intent.hasExtra("id")) {
            val id = intent.getSerializableExtra("id") as String

            database.child("offers").child(id).get().addOnSuccessListener {
                val foundOffer = it.getValue(Offer::class.java)

                if (foundOffer != null) {
                    offer = foundOffer

                    initializeFields()
                    configDeleteButton()
                    configEditButton()
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

            if (market != null) {
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
        database.child("offers").child(offer.uid).removeValue()
        Toast.makeText(
            this,
            "Oferta deletada com sucesso!",
            Toast.LENGTH_SHORT,
        ).show()

        finish()
    }

    private fun configDeleteButton() {
        if (userId != null && offer.userId == userId) {
            deleteBtn.setOnClickListener {
                deleteOffer()
            }
        } else {
            deleteBtn.visibility = View.GONE
        }
    }

    private fun configEditButton() {
        if (userId != null && offer.userId == userId) {
            editBtn.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragmentToLoad", "offer")
                intent.putExtra("offer", offer)

                startActivity(intent)
            }
        } else {
            editBtn.visibility = View.GONE
        }
    }

}
