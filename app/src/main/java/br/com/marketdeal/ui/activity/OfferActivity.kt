package br.com.marketdeal.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.model.Offer
import br.com.marketdeal.utils.CurrencyConverter
import br.com.marketdeal.utils.ImageLoader

class OfferActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }
    private val userId by lazy { Firebase.auth.currentUser?.uid }

    private val image by lazy { findViewById<ShapeableImageView>(R.id.activity_offer_image) }
    private val spinner by lazy { findViewById<ProgressBar>(R.id.activity_offer_spinner) }
    private val title by lazy { findViewById<TextView>(R.id.activity_offer_title) }
    private val date by lazy { findViewById<TextView>(R.id.activity_offer_date) }
    private val size by lazy { findViewById<TextView>(R.id.activity_offer_size) }
    private val originalPrice by lazy { findViewById<TextView>(R.id.activity_offer_original_price) }
    private val currentPrice by lazy { findViewById<TextView>(R.id.activity_offer_current_price) }
    private val observations by lazy { findViewById<TextView>(R.id.activity_offer_observations) }

    private val marketName by lazy { findViewById<TextView>(R.id.activity_offer_market_name) }
    private val marketStreetAndNumber by lazy { findViewById<TextView>(R.id.activity_offer_market_streetAndNumber) }
    private val marketNeighborhoodAndCity by lazy { findViewById<TextView>(R.id.activity_offer_market_neighborhoodAndCity) }

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

                    ImageLoader.loadImage(this, offer.imageUrl, image, spinner)
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
                marketStreetAndNumber.text = market.street + ", N°: " + market.number
                marketNeighborhoodAndCity.text = market.neighborhood + ", " + market.city
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting market data", it)
        }
    }

    private fun initializeFields() {
        title.text = offer.productName
        date.text = offer.createdAt
        size.text = offer.size
        originalPrice.text = CurrencyConverter.convertToReal(offer.originalPrice)
        currentPrice.text = CurrencyConverter.convertToReal(offer.currentPrice)
        observations.text = offer.observations
    }

    private fun configDeleteButton() {
        if (userId != null && offer.userId == userId) {
            deleteBtn.setOnClickListener {
                it.isEnabled = false
                it.isClickable = false

                deleteDialog()
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

    private fun deleteDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Deseja realmente apagar esse produto?")
            .setTitle("Excluir Produto")
            .setPositiveButton("Confirmar") { _, _ ->
                deleteOffer { offerWasSuccessfullyDeleted ->
                    if (offerWasSuccessfullyDeleted) {
                        finish()
                    }
                }
            }
            .setNegativeButton("Cancelar") { _, _ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        enableDeleteButton()
    }

    private fun deleteOffer(callback: (Boolean) -> Unit) {
        database.child("offers").child(offer.uid).removeValue()
            .addOnSuccessListener {
                showToast("Oferta deletada com sucesso!")
                callback(true)
            }
            .addOnFailureListener {
                showToast("Falha ao deletar a oferta")
                callback(false)
            }
    }

    private fun enableDeleteButton() {
        deleteBtn.isEnabled = true
        deleteBtn.isClickable = true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
