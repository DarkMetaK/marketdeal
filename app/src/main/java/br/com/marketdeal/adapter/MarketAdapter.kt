package br.com.marketdeal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Market

class MarketAdapter(private val context: Context) : BaseAdapter() {
    private val markets = ArrayList<Market>()

    private fun addAll(items: List<Market>) {
        markets.addAll(items)
        notifyDataSetChanged()
    }

    fun update(items: List<Market>) {
        clear()
        addAll(items)
    }

    private fun clear() {
        markets.clear()
    }

    override fun getCount(): Int {
        return markets.size
    }

    override fun getItem(position: Int): Market {
        return markets[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val createdView = LayoutInflater
            .from(context)
            .inflate(R.layout.item_market, viewGroup, false)

        val mkt = markets[position]

        Log.i(mkt.name, "name")
        Log.i(mkt.neighborhood, "neighborhood")
        Log.i(mkt.city, "city")

        val name = createdView.findViewById<TextView>(R.id.item_market_name)
        val city = createdView.findViewById<TextView>(R.id.item_market_city)
        val neighborhood= createdView.findViewById<TextView>(R.id.item_market_neighborhood)
        val number = createdView.findViewById<TextView>(R.id.item_market_number)
        val street = createdView.findViewById<TextView>(R.id.item_market_street)

        name.text = mkt.name
        city.text = mkt.city
        neighborhood.text = mkt.neighborhood
        number.text = mkt.number
        street.text = mkt.street

        return createdView
    }

}