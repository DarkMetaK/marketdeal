package br.com.marketdeal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import br.com.marketdeal.R
import br.com.marketdeal.adapter.OfferAdapter
import br.com.marketdeal.model.Offer
import br.com.marketdeal.ui.activity.OfferActivity

class HomeFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }

    private lateinit var offerAdapter: OfferAdapter
    private val offerListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val offerArray = ArrayList<Offer>()

            for (suggestionSnapshot in dataSnapshot.children) {
                val offer = suggestionSnapshot.getValue(Offer::class.java)

                if (offer != null) {
                    offerArray.add(offer)
                }
            }

            offerAdapter.update(offerArray)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadOffers:onCancelled", databaseError.toException())
        }
    }

    private lateinit var offerList: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initializeAndConfigList(view)
        database.child("offers").addValueEventListener(offerListener)
        return view
    }

    private fun initializeAndConfigList(view: View) {
        offerAdapter = OfferAdapter(view.context)
        offerList = view.findViewById(R.id.fragment_home_offers_list)

        offerList.adapter = offerAdapter
        configListItemClick()
    }

    private fun configListItemClick() {
        offerList.setOnItemClickListener { adapterView, view, position, _ ->
            val offer = adapterView.getItemAtPosition(position) as Offer
            val intent = Intent(view.context, OfferActivity::class.java)
            intent.putExtra("id", offer.uid)

            startActivity(intent)
        }
    }

}