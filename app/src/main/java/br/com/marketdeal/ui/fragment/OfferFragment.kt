package br.com.marketdeal.ui.fragment

import java.time.LocalDate
import java.util.UUID

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import br.com.marketdeal.R
import br.com.marketdeal.dto.MarketSpinnerDTO
import br.com.marketdeal.dto.ProductSpinnerDTO
import br.com.marketdeal.model.Offer

class OfferFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }
    private val auth by lazy { Firebase.auth }

    private val navController by lazy { requireActivity().findNavController(R.id.activity_main_container) }
    private lateinit var offer: Offer
    private var offerIsBeingEdited = false

    private lateinit var sizeLayout: TextInputLayout
    private lateinit var size: TextInputEditText

    private lateinit var originalPriceLayout: TextInputLayout
    private lateinit var originalPrice: TextInputEditText

    private lateinit var currentPriceLayout: TextInputLayout
    private lateinit var currentPrice: TextInputEditText

    private lateinit var observations: TextInputEditText
    private lateinit var submitBtn: Button

    private lateinit var productAutocompleteLayout: TextInputLayout
    private lateinit var productAutocomplete: AutoCompleteTextView
    private lateinit var productAutoCompleteAdapter: ArrayAdapter<String>
    private var selectedProduct: ProductSpinnerDTO? = null
    private val productList = ArrayList<ProductSpinnerDTO>()
    private val productListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (suggestionSnapshot in dataSnapshot.children) {
                val name = suggestionSnapshot.child("name").getValue(String::class.java)
                val imageUrl = suggestionSnapshot.child("imageUrl").getValue(String::class.java)

                val dto = ProductSpinnerDTO(
                    suggestionSnapshot.key.toString(),
                    name!!,
                    imageUrl
                )

                productList.add(dto)
                productAutoCompleteAdapter.add(name)
            }

            productAutoCompleteAdapter.filter.filter("")

            if (offerIsBeingEdited) {
                val product = productList.find { dto -> dto.id == offer.productId }

                if (product != null) {
                    val dto = ProductSpinnerDTO(product.id, product.name, product.imageUrl)
                    selectedProduct = dto
                    productAutocomplete.setText(dto.toString(), false)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    private lateinit var marketAutocompleteLayout: TextInputLayout
    private lateinit var marketAutocomplete: AutoCompleteTextView
    private lateinit var marketAutoCompleteAdapter: ArrayAdapter<String>
    private var selectedMarket: MarketSpinnerDTO? = null
    private val marketList = ArrayList<MarketSpinnerDTO>()
    private val marketListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (suggestionSnapshot in dataSnapshot.children) {
                val name = suggestionSnapshot.child("name").getValue(String::class.java)
                val dto = MarketSpinnerDTO(
                    suggestionSnapshot.key.toString(),
                    name!!
                )

                marketList.add(dto)
                marketAutoCompleteAdapter.add(name)
            }

            marketAutoCompleteAdapter.filter.filter("")

            if (offerIsBeingEdited) {
                val market = marketList.find { dto -> dto.id == offer.marketId }

                if (market != null) {
                    val dto = MarketSpinnerDTO(market.id, market.name)
                    selectedMarket = dto
                    marketAutocomplete.setText(dto.toString(), false)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadMarkets:onCancelled", databaseError.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offer, container, false)

        initializeFields(view)
        verifyIfOfferIsBeingEdited()

        configSubmitBtn()
        configProductAutoComplete(view)
        configMarketAutoComplete(view)

        database.child("products").addValueEventListener(productListener)
        database.child("markets").addValueEventListener(marketListener)

        return view
    }

    override fun onPause() {
        super.onPause()

        resetErrors()

        if (offerIsBeingEdited) {
            resetForm()
            offerIsBeingEdited = false
            activity?.intent?.removeExtra("offer")
        }
    }

    private fun initializeFields(view: View) {
        sizeLayout = view.findViewById(R.id.fragment_offer_size_layout)
        size = view.findViewById(R.id.fragment_offer_size)

        originalPriceLayout = view.findViewById(R.id.fragment_offer_original_price_layout)
        originalPrice = view.findViewById(R.id.fragment_offer_original_price)

        currentPriceLayout = view.findViewById(R.id.fragment_offer_current_price_layout)
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

            submitBtn.text = "Editar Oferta"
            offer = intentOffer
            offerIsBeingEdited = true
        }
    }

    private fun configProductAutoComplete(view: View) {
        productAutoCompleteAdapter = ArrayAdapter(
            view.context,
            R.layout.item_autocomplete
        )

        productAutocompleteLayout = view.findViewById(R.id.fragment_offer_product_layout)
        productAutocomplete = view.findViewById(R.id.fragment_offer_product)
        productAutocomplete.setAdapter(productAutoCompleteAdapter)
        productAutocomplete.setOnItemClickListener { _, _, position, _ ->
            selectedProduct = productList[position]
        }
    }

    private fun configMarketAutoComplete(view: View) {
        marketAutoCompleteAdapter = ArrayAdapter(
            view.context,
            R.layout.item_autocomplete
        )

        marketAutocompleteLayout = view.findViewById(R.id.fragment_offer_market_layout)
        marketAutocomplete = view.findViewById(R.id.fragment_offer_market)
        marketAutocomplete.setAdapter(marketAutoCompleteAdapter)
        marketAutocomplete.setOnItemClickListener { _, _, position, _ ->
            selectedMarket = marketList[position]
        }
    }

    private fun configSubmitBtn() {
        submitBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            createOfferModel { offerWasCreated ->
                if (offerWasCreated) {
                    if (offerIsBeingEdited) {
                        updateOffer()
                    } else {
                        addNewOffer()
                    }
                } else {
                    enableSubmitButton()
                }
            }
        }
    }

    private fun addNewOffer() {
        database.child("offers").child(offer.uid).setValue(offer)
            .addOnSuccessListener {
                showToast("Oferta anunciada com sucesso!")
                cleanFields()
            }
            .addOnFailureListener {
                showToast("Falha ao anunciar oferta, por favor tente novamente!")
            }
            .addOnCompleteListener {
                enableSubmitButton()
            }
    }

    private fun updateOffer() {
        database.child("offers").child(offer.uid).setValue(offer)
            .addOnSuccessListener {
                showToast("Oferta atualizada com sucesso!")

                cleanFields()
                navController.navigate(R.id.mi_home)
            }
            .addOnFailureListener {
                showToast("Falha ao atualizar os dados, por favor tente novamente.")
            }
            .addOnCompleteListener {
                enableSubmitButton()
            }
    }

    private fun createOfferModel(callback: (Boolean) -> Unit) {
        if (!validateFields()) {
            showToast("Preencha todos os campos obrigatórios.")
            callback(false)
            return
        }

        var offerId = UUID.randomUUID().toString()
        val sizeStr = size.text.toString()
        val originalPriceStr = originalPrice.text.toString()
        val currentPriceStr = currentPrice.text.toString()
        val observationsStr = observations.text.toString()
        val userId = auth.currentUser!!.uid
        val marketId = selectedMarket!!.id
        val marketName = selectedMarket!!.name
        val productId = selectedProduct!!.id
        val productName = selectedProduct!!.name
        val imageUrl = selectedProduct!!.imageUrl

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
            imageUrl,
            userId
        )

        callback(true)
    }

    private fun validateFields(): Boolean {
        var amountOfErrors = 0
        val sizeStr = size.text.toString()
        val originalPriceStr = originalPrice.text.toString()
        val currentPriceStr = currentPrice.text.toString()

        if (selectedProduct == null) {
            productAutocompleteLayout.error = "O produto é obrigatório."
            amountOfErrors++
        } else {
            productAutocompleteLayout.error = null
        }

        if (selectedMarket == null) {
            marketAutocompleteLayout.error = "O mercado é obrigatório."
            amountOfErrors++
        } else {
            marketAutocompleteLayout.error = null
        }

        if (sizeStr.isEmpty()) {
            sizeLayout.error = "O tamanho é obrigatório."
            amountOfErrors++
        } else {
            sizeLayout.error = null
        }

        if (originalPriceStr.isEmpty()) {
            originalPriceLayout.error = "O preço original é obrigatório."
            amountOfErrors++
        } else {
            originalPriceLayout.error = null
        }

        if (currentPriceStr.isEmpty()) {
            currentPriceLayout.error = "O preço atual é obrigatório."
            amountOfErrors++
        } else {
            currentPriceLayout.error = null
        }

        if (originalPriceStr.isNotEmpty() && currentPriceStr.isNotEmpty()) {
            if (currentPriceStr.toDouble() >= originalPriceStr.toDouble()) {
                currentPriceLayout.error = "O preço atual não poder ser maior/igual ao preço original."
                amountOfErrors++
            } else {
                currentPriceLayout.error = null
            }
        }

        return amountOfErrors == 0
    }

    private fun cleanFields() {
        productAutocomplete.setText(null, false)
        marketAutocomplete.setText(null, false)
        selectedProduct = null
        selectedMarket = null

        size.setText("")
        originalPrice.setText("")
        currentPrice.setText("")
        observations.setText("")
    }

    private fun resetErrors() {
        productAutocompleteLayout.error = null
        marketAutocompleteLayout.error = null
        sizeLayout.error = null
        originalPriceLayout.error = null
        currentPriceLayout.error = null
    }

    private fun resetForm() {
        cleanFields()
        submitBtn.text = "Anunciar Oferta"
    }

    private fun enableSubmitButton() {
        submitBtn.isEnabled = true
        submitBtn.isClickable = true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
