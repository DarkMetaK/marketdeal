package br.com.marketdeal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.adapter.MarketAdapter
import br.com.marketdeal.model.Market
import br.com.marketdeal.model.Offer
import br.com.marketdeal.ui.activity.MarketActivity
import br.com.marketdeal.ui.activity.MarketFormActivity
import br.com.marketdeal.ui.activity.OfferActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StoreFragment : Fragment(R.layout.fragment_store) {
    private lateinit var AddMarketBtn: FloatingActionButton
    private val database by lazy { Firebase.database.reference }
    private lateinit var marketAdapter: MarketAdapter
    private val marketListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val marketArray = ArrayList<Market>()

            for (suggestionSnapshot in dataSnapshot.getChildren()) {
                val market = suggestionSnapshot.getValue(Market::class.java);

                if (market != null) {
                    marketArray.add(market)
                }
            }

            marketAdapter.update(marketArray)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("firebase", "loadProducts:onCancelled", databaseError.toException())
        }
    }

    private lateinit var marketList: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_store, container, false)

        initializeAndConfigList(view)
        database.child("markets").addValueEventListener(marketListener)
        initializeFields(view)
        AddMarketBtnConfig()
        configListItemClick()
        return view
    }
    private fun initializeFields(view: View) {
        AddMarketBtn = view.findViewById(R.id.floatingAddMarket)
    }
    private fun initializeAndConfigList(view: View) {
        marketAdapter = MarketAdapter(view.context)
        marketList = view.findViewById(R.id.fragment_market_list)

        marketList.adapter = marketAdapter
    }

    private fun AddMarketBtnConfig() {
        AddMarketBtn.setOnClickListener {
            val intent = Intent(requireActivity(), MarketFormActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
    private fun configListItemClick() {

        marketList.setOnItemClickListener { adapterView, view, position, id ->
            val market = adapterView.getItemAtPosition(position) as Market
            val intent = Intent(view.context, MarketActivity::class.java)
            intent.putExtra("id", market.id)

            startActivity(intent)
        }
    }

}