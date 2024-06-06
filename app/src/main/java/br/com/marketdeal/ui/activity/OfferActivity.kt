package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Offer
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OfferActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }

    private val title by lazy { findViewById<TextView>(R.id.activity_offer_title) }
    private val date by lazy { findViewById<TextView>(R.id.activity_offer_date) }
    private val size by lazy { findViewById<TextView>(R.id.activity_offer_size) }
    private val originalPrice by lazy { findViewById<TextView>(R.id.activity_offer_original_price) }
    private val currentPrice by lazy { findViewById<TextView>(R.id.activity_offer_current_price) }
    private val observations by lazy { findViewById<TextView>(R.id.activity_offer_observations) }

    private lateinit var offer: Offer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_offer)

        retrieveIdAndFetchOfferData()
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
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        } else {
            finish()
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

}