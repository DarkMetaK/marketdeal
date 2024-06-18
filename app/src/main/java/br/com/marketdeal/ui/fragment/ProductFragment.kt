package br.com.marketdeal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import br.com.marketdeal.R
import br.com.marketdeal.adapter.ProductAdapter
import br.com.marketdeal.model.Product
import br.com.marketdeal.ui.activity.ProductFormActivity

class ProductFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }

    private lateinit var productList: ListView
    private lateinit var fab: FloatingActionButton

    private lateinit var productAdapter: ProductAdapter
    private val productListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val productArray = ArrayList<Product>()

            for (suggestionSnapshot in dataSnapshot.children) {
                val product = suggestionSnapshot.getValue(Product::class.java);

                if (product != null) {
                    productArray.add(product)
                }
            }

            productAdapter.update(productArray)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        initializeFields(view)
        configFab()
        configProductList()

        database.child("products").addValueEventListener(productListener)
        return view
    }

    private fun initializeFields(view: View) {
        productAdapter = ProductAdapter(view.context)

        productList = view.findViewById(R.id.fragment_product_list)
        fab = view.findViewById(R.id.fragment_product_fab)
    }

    private fun configFab() {
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), ProductFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configProductList() {
        productList.adapter = productAdapter
        configListItemClick()
    }

    private fun configListItemClick() {
        productList.setOnItemClickListener { adapterView, view, position, _ ->
            val product = adapterView.getItemAtPosition(position) as Product
            val intent = Intent(view.context, ProductFormActivity::class.java)
            intent.putExtra("product", product)

            startActivity(intent)
        }
    }

}