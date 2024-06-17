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
import br.com.marketdeal.ui.activity.MarketFormActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MarketFragment : Fragment() {
    private val database by lazy { Firebase.database.reference }

    private lateinit var addMarketBtn: FloatingActionButton
    private lateinit var marketAdapter: MarketAdapter
    private val marketListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val marketArray = ArrayList<Market>()

            for (suggestionSnapshot in dataSnapshot.children) {
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
        val view = inflater.inflate(R.layout.fragment_market, container, false)

        initializeFields(view)
        initializeAndConfigList(view)
        addMarketBtnConfig()

        database.child("markets").addValueEventListener(marketListener)

        return view
    }

    private fun initializeFields(view: View) {
        addMarketBtn = view.findViewById(R.id.fragment_market_fab)
    }

    private fun initializeAndConfigList(view: View) {
        marketAdapter = MarketAdapter(view.context)
        marketList = view.findViewById(R.id.fragment_market_list)

        marketList.adapter = marketAdapter
    }

    private fun addMarketBtnConfig() {
        addMarketBtn.setOnClickListener {
            val intent = Intent(requireActivity(), MarketFormActivity::class.java)
            startActivity(intent)
        }
    }

}