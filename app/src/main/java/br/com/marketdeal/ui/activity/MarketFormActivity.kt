package br.com.marketdeal.ui.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.utils.CepSearcher
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.util.UUID

class MarketFormActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }
    private val storageRef by lazy { Firebase.storage.reference }

    private lateinit var market: Market
    private var imageUrl: Uri? = null
    private var marketIsBeingEdited = false

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            image.setImageURI(uri)
            imageUrl = uri
        }
    }

    private val image by lazy { findViewById<ShapeableImageView>(R.id.activity_market_form_image) }
    private val spinner by lazy { findViewById<ProgressBar>(R.id.activity_market_form_spinner) }
    private val imageBtn by lazy { findViewById<TextView>(R.id.activity_market_form_add_image) }

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
        configImageBtn()
        checkIfEditingMarket()
    }

    private fun configImageBtn() {
        imageBtn.setOnClickListener {
            pickMedia.launch("image/*")
        }
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
            createOrUpdateMarket()
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
    private fun uploadImage(callback: (Boolean) -> Unit) {
        val imgRef = storageRef.child("images/${UUID.randomUUID()}")
        imgRef.putFile(imageUrl!!)
            .addOnSuccessListener {
                imgRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri
                    callback(true)
                }.addOnFailureListener {
                    showToast("Falha ao obter a URL da foto!")
                    callback(false)
                }
            }
            .addOnFailureListener {
                showToast("Falha ao carregar a foto!")
                callback(false)
            }
    }
    private fun createOrUpdateMarket() {
        val nameStr = name.text.toString()
        val numberStr = number.text.toString()
        val streetStr = street.text.toString()
        val neighborhoodStr = neighborhood.text.toString()
        val cityStr = city.text.toString()

        if (nameStr.isBlank() || numberStr.isBlank() || streetStr.isBlank() || neighborhoodStr.isBlank() || cityStr.isBlank()) {
            showToast("Preencha todos os campos")
            return
        }

        if (imageUrl != null) {
            uploadImage { imageUploadSuccess ->
                if (imageUploadSuccess) {
                    market = Market(
                        UUID.randomUUID().toString(),
                        imageUrl.toString(),
                        nameStr,
                        cityStr,
                        neighborhoodStr,
                        streetStr,
                        numberStr
                    )

                    registerOrUpdateMarket()
                } else {
                    showToast("Falha ao realizar upload da imagem.")
                }
            }
        } else {
            market = Market(
                UUID.randomUUID().toString(),
                null, // ou imageUrl?.toString() caso queira manter nulo quando não há imagem
                nameStr,
                cityStr,
                neighborhoodStr,
                streetStr,
                numberStr
            )

            registerOrUpdateMarket()
        }
    }

    private fun registerOrUpdateMarket() {
        if (marketIsBeingEdited) {
            updateMarket()
        } else {
            registerMarket()
        }
    }

    private fun registerMarket() {
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

    private fun updateMarket() {
        database.child("markets").child(market.uid).setValue(market)
            .addOnSuccessListener {
                showToast("Mercado atualizado com sucesso!")
                cleanFields()
                finish()
            }
            .addOnFailureListener {
                showToast("Falha ao atualizar mercado, por favor tente novamente!")
            }
    }

    private fun cleanFields() {
        cep.setText("")
        street.setText("")
        name.setText("")
        neighborhood.setText("")
        city.setText("")
        number.setText("")
        image.setImageResource(R.drawable.ic_empty_image) // Adicione um placeholder de imagem se necessário
        imageUrl = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkIfEditingMarket() {
        val intentMarket = intent?.extras?.getSerializable("market") as Market?

        if (intentMarket != null) {
            marketIsBeingEdited = true
            market = intentMarket

            name.setText(market.name)
            cep.setText("") // Se necessário, você pode carregar o CEP no campo aqui
            street.setText(market.street)
            number.setText(market.number)
            neighborhood.setText(market.neighborhood)
            city.setText(market.city)

            imageUrl = market.imageUrl?.toUri()
            imageUrl?.let { image.setImageURI(it) }

            registerBtn.text = "Atualizar"
        }
    }
}
