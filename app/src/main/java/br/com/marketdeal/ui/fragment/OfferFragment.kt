package br.com.marketdeal.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OfferFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }
    private val productListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
            for (suggestionSnapshot in dataSnapshot.getChildren()) {
                //Get the suggestion by childing the key of the string you want to get.
                val suggestion = suggestionSnapshot.child("name").getValue(
                    String::class.java
                )
                //Add the retrieved string to the list
                productsAutoCompleteAdapter?.add(suggestion)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }
    private val marketListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
            for (suggestionSnapshot in dataSnapshot.getChildren()) {
                //Get the suggestion by childing the key of the string you want to get.
                val suggestion = suggestionSnapshot.child("name").getValue(
                    String::class.java
                )
                //Add the retrieved string to the list
                marketsAutoCompleteAdapter?.add(suggestion)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    private lateinit var productField: Spinner
    private lateinit var productsAutoCompleteAdapter: ArrayAdapter<String>
    private lateinit var marketField: Spinner
    private lateinit var marketsAutoCompleteAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.activity_offer, container, false)

        configProductAutoComplete(view)
        configMarketAutoComplete(view)
        database.child("products").addValueEventListener(productListener)
        database.child("markets").addValueEventListener(marketListener)

        return view
    }

    private fun configProductAutoComplete(view: View) {
        productsAutoCompleteAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1
        )
        productField = view.findViewById(R.id.activity_offer_product)
        productField.adapter = productsAutoCompleteAdapter
    }

    private fun configMarketAutoComplete(view: View) {
        marketsAutoCompleteAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1
        )
        marketField = view.findViewById(R.id.activity_offer_market)
        marketField.adapter = marketsAutoCompleteAdapter
    }


}