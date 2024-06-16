package br.com.marketdeal.ui.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Product
import br.com.marketdeal.utils.ImageLoader
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class ProductFormActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }
    private val storageRef by lazy { Firebase.storage.reference }

    private lateinit var product: Product
    private var productIsBeingEdited = false

    private var imageUrl: Uri? = null
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            image.setImageURI(uri)
            imageUrl = uri
        }
    }

    private val image by lazy { findViewById<ShapeableImageView>(R.id.activity_product_form_image) }
    private val spinner by lazy { findViewById<ProgressBar>(R.id.activity_product_form_spinner) }

    private val nameLayout by lazy { findViewById<TextInputLayout>(R.id.activity_product_form_name_layout) }
    private val name by lazy { findViewById<TextInputEditText>(R.id.activity_product_form_name) }

    private val producerLayout by lazy { findViewById<TextInputLayout>(R.id.activity_product_form_producer_layout) }
    private val producer by lazy { findViewById<TextInputEditText>(R.id.activity_product_form_producer) }

    private val description by lazy { findViewById<TextInputEditText>(R.id.activity_product_form_description) }
    private val submitBtn by lazy { findViewById<Button>(R.id.activity_product_form_btn) }
    private val imageBtn by lazy { findViewById<TextView>(R.id.activity_product_form_add_image) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_form)

        verifyIfProductIsBeingEdited()
        configImageBtn()
        configSubmitBtn()
    }

    private fun verifyIfProductIsBeingEdited() {
        val intentProduct = intent?.extras?.getSerializable("product") as Product?

        if (intentProduct != null) {
            if (intentProduct.imageUrl != null) {
                ImageLoader.loadImage(this, intentProduct.imageUrl, image, spinner)
            }

            name.setText(intentProduct.name)
            producer.setText(intentProduct.producer)
            description.setText(intentProduct.description)
            submitBtn.text = "Editar Produto"

            product = intentProduct
            productIsBeingEdited = true
        }
    }

    private fun configImageBtn() {
        imageBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    private fun configSubmitBtn() {
        submitBtn.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false

            createProductModel { productWasCreated ->
                if (productWasCreated) {
                    if (productIsBeingEdited) {
                        updateProduct()
                    } else {
                        addNewProduct()
                    }
                } else {
                    it.isEnabled = true
                    it.isClickable = true
                }
            }
        }
    }

    private fun addNewProduct() {
        database.child("products").child(product.uid).setValue(product)
            .addOnSuccessListener {
                showToast("Produto adicionado com sucesso!")
                cleanFields()
            }
            .addOnFailureListener {
                showToast("Falha ao adicionar produto, por favor tente novamente!")
            }
            .addOnCompleteListener {
                enableSubmitButton()
            }
    }

    private fun updateProduct() {
        database.child("products").child(product.uid).setValue(product)
            .addOnSuccessListener {
                showToast("Produto atualizado com sucesso!")
                intent.removeExtra("product")
                finish()
            }
            .addOnFailureListener {
                showToast("Falha ao atualizar produto, por favor tente novamente!")
            }
            .addOnCompleteListener {
                enableSubmitButton()
            }
    }

    private fun createProductModel(callback: (Boolean) -> Unit) {
        var productId = UUID.randomUUID().toString()
        val nameStr = name.text.toString()
        val producerStr = producer.text.toString()
        val descriptionStr = description.text.toString()

        if (nameStr.isEmpty()) {
            nameLayout.error = "O nome é obrigatório."
        } else {
            nameLayout.error = null
        }

        if (producerStr.isEmpty()) {
            producerLayout.error = "A marca é obrigatória."
        } else {
            producerLayout.error = null
        }

        if (nameStr.isEmpty() || producerStr.isEmpty()) {
            showToast("Preencha todos os campos obrigatórios.")
            callback(false)
            return
        }

        if (productIsBeingEdited) {
            productId = product.uid
        }

        if (imageUrl != null) {
            uploadImage { imageUploadSuccess ->
                if (imageUploadSuccess) {
                    product = Product(
                        productId,
                        imageUrl.toString(),
                        nameStr,
                        producerStr,
                        descriptionStr
                    )
                    callback(true)
                } else {
                    callback(false)
                }
            }
        } else {
            product = Product(
                productId,
                null,
                nameStr,
                producerStr,
                descriptionStr
            )
            callback(true)
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

    private fun cleanFields() {
        image.setImageResource(R.drawable.ic_empty_image)
        name.setText("")
        producer.setText("")
        description.setText("")
        imageUrl = null
    }

    private fun enableSubmitButton() {
        submitBtn.isEnabled = true
        submitBtn.isClickable = true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
