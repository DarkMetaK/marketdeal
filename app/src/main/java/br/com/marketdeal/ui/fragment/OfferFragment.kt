package br.com.marketdeal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.dto.MarketSpinnerDTO
import br.com.marketdeal.dto.ProductSpinnerDTO
import br.com.marketdeal.model.Offer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.UUID

class OfferFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }
    private val auth by lazy { Firebase.auth }

    private lateinit var size: EditText
    private lateinit var originalPrice: EditText
    private lateinit var currentPrice: EditText
    private lateinit var observations: EditText
    private lateinit var submitBtn: Button

    private lateinit var productSpinner: Spinner
    private lateinit var productAutoCompleteAdapter: ArrayAdapter<String>
    private val productList = ArrayList<ProductSpinnerDTO>()
    private val productListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (suggestionSnapshot in dataSnapshot.getChildren()) {
                val name = suggestionSnapshot.child("name").getValue(String::class.java)
                val dto = ProductSpinnerDTO(
                    suggestionSnapshot.key.toString(),
                    name!!
                )

                productList.add(dto)
                productAutoCompleteAdapter.add(name)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    private lateinit var marketSpinner: Spinner
    private lateinit var marketAutoCompleteAdapter: ArrayAdapter<String>
    private val marketList = ArrayList<MarketSpinnerDTO>()
    private val marketListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (suggestionSnapshot in dataSnapshot.getChildren()) {
                val name = suggestionSnapshot.child("name").getValue(String::class.java)
                val dto = MarketSpinnerDTO(
                    suggestionSnapshot.key.toString(),
                    name!!
                )

                marketList.add(dto)
                marketAutoCompleteAdapter.add(name)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    private lateinit var offer: Offer
    private var offerIsBeingEdited = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_offer, container, false)

        initializeFields(view)
        verifyIfOfferIsBeingEdited()

        configSubmitBtn()
        configProductAutoComplete(view)
        configMarketAutoComplete(view)
        database.child("products").addValueEventListener(productListener)
        database.child("markets").addValueEventListener(marketListener)

        return view
    }

    private fun initializeFields(view: View) {
        size = view.findViewById(R.id.fragment_offer_size)
        originalPrice = view.findViewById(R.id.fragment_offer_original_price)
        currentPrice = view.findViewById(R.id.fragment_offer_current_price)
        observations = view.findViewById(R.id.fragment_offer_observations)
        submitBtn = view.findViewById(R.id.fragment_offer_submit_btn)
    }

    private fun verifyIfOfferIsBeingEdited() {
        val intentOffer = activity?.intent?.extras?.getSerializable("offer") as Offer?

        if (intentOffer != null) {
            size.setText(intentOffer.size)
            originalPrice.setText(intentOffer.originalPrice.toString())
            currentPrice.setText(intentOffer.currentPrice.toString())
            observations.setText(intentOffer.observations)

            var market = marketList.find { dto -> dto.id == intentOffer.marketId }
            var product = productList.find { dto -> dto.id === intentOffer.productId }

            if (market != null) {
                var marketIndex = marketList.indexOf(market)
                marketSpinner.setSelection(marketIndex)
            }

            if (product != null) {
                var productIndex = productList.indexOf(product)
                productSpinner.setSelection(productIndex)
            }

            submitBtn.text = "Editar Oferta"
            offer = intentOffer
            offerIsBeingEdited = true
        }
    }

    private fun configSubmitBtn() {
        submitBtn.setOnClickListener {
            createOfferModel()

            if (offer != null) {
                if (offerIsBeingEdited) {
                    updateOffer()
                } else {
                    addNewOffer()
                }
            }
        }
    }

    private fun addNewOffer() {
        database.child("offers").child(offer.uid).setValue(offer)
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity(),
                    "Oferta anunciada com sucesso!",
                    Toast.LENGTH_SHORT,
                ).show()
                cleanFields()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireActivity(),
                    "Falha ao anunciar oferta, por favor tente novamente!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private fun updateOffer() {
        val offerValues = offer.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/${offer.uid}" to offerValues,
        )

        database.child("offers").updateChildren(childUpdates)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireActivity(),
                        "Oferta atualizada com sucesso!",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Falha ao atualizar os dados, por favor tente novamente.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun createOfferModel() {
        var offerId = UUID.randomUUID().toString()
        val sizeStr = size.text.toString()
        val originalPriceStr = originalPrice.text.toString()
        val currentPriceStr = currentPrice.text.toString()
        val observationsStr = observations.text.toString()
        val marketId = marketList[marketSpinner.selectedItemId.toInt()].id
        val marketName = marketList[marketSpinner.selectedItemId.toInt()].name
        val productId = productList[productSpinner.selectedItemId.toInt()].id
        val productName = productList[productSpinner.selectedItemId.toInt()].name
        val userId = auth.currentUser!!.uid

        if (sizeStr.isEmpty() || originalPriceStr.isEmpty() || currentPriceStr.isEmpty() || marketId.isEmpty() || productId.isEmpty()) {
            Toast.makeText(
                requireActivity(),
                "Preencha todos os campos",
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        if (offerIsBeingEdited) {
            offerId = offer.uid
        }

        offer = Offer(
            offerId,
            sizeStr,
            originalPriceStr.toDouble(),
            currentPriceStr.toDouble(),
            observationsStr,
            LocalDate.now().toString(),
            marketId,
            marketName,
            productId,
            productName,
            userId
        )
    }

    private fun configProductAutoComplete(view: View) {
        productAutoCompleteAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1
        )

        productSpinner = view.findViewById(R.id.fragment_offer_product)
        productSpinner.adapter = productAutoCompleteAdapter
    }

    private fun configMarketAutoComplete(view: View) {
        marketAutoCompleteAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1
        )
        marketSpinner = view.findViewById(R.id.fragment_offer_market)
        marketSpinner.adapter = marketAutoCompleteAdapter
    }

    private fun cleanFields() {
        size.setText("")
        originalPrice.setText("")
        currentPrice.setText("")
        observations.setText("")
    }

}