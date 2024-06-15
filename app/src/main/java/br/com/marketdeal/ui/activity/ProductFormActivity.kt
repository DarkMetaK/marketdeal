package br.com.marketdeal.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marketdeal.R
import br.com.marketdeal.model.Product
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ProductFormActivity : AppCompatActivity() {
    private val database by lazy { Firebase.database.reference }

    private lateinit var product: Product
    private var productIsBeingEdited = false

    private val name by lazy { findViewById<EditText>(R.id.activity_product_form_name) }
    private val producer by lazy { findViewById<EditText>(R.id.activity_product_form_producer) }
    private val description by lazy { findViewById<EditText>(R.id.activity_product_form_description) }
    private val submitBtn by lazy { findViewById<Button>(R.id.activity_product_form_btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_form)

        verifyIfProductIsBeingEdited()
        configSubmitBtn()
    }

    private fun verifyIfProductIsBeingEdited() {
        val intentProduct = intent?.extras?.getSerializable("product") as Product?

        if (intentProduct != null) {
            name.setText(intentProduct.name)
            producer.setText(intentProduct.producer)
            description.setText(intentProduct.description)
            submitBtn.text = "Editar Produto"

            product = intentProduct
            productIsBeingEdited = true
        }
    }

    private fun configSubmitBtn() {
        submitBtn.setOnClickListener {
            val productWasCreated = createProductModel()

            if (productWasCreated) {
                if (productIsBeingEdited) {
                    //updateOffer()
                } else {
                    addNewProduct()
                }
            }
        }
    }

    private fun addNewProduct() {
        database.child("products").child(product.uid).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Produto adicionado com sucesso!",
                    Toast.LENGTH_SHORT,
                ).show()
                cleanFields()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Falha ao adicionar produto, por favor tente novamente!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private fun createProductModel(): Boolean {
        var productId = UUID.randomUUID().toString()
        val nameStr = name.text.toString()
        val producerStr = producer.text.toString()
        val descriptionStr = description.text.toString()

        if (nameStr.isEmpty() || producerStr.isEmpty()) {
            Toast.makeText(
                this,
                "Preencha todos os campos obrigatórios.",
                Toast.LENGTH_SHORT,
            ).show()

            return false
        }

        if (productIsBeingEdited) {
            productId = product.uid
        }

        product = Product(
            productId,
            nameStr,
            producerStr,
            descriptionStr
        )

        return true
    }

    private fun cleanFields() {
        name.setText("")
        producer.setText("")
        description.setText("")
    }

}
