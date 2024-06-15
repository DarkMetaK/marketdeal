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
            .inflate(R.layout.market_item, viewGroup, false)

        val mkt = markets[position]

        Log.i(mkt.name, "name")
        Log.i(mkt.neighborhood, "neighborhood")
        Log.i(mkt.city, "city")

        val name = createdView.findViewById<TextView>(R.id.market_name_item)
        val city = createdView.findViewById<TextView>(R.id.market_city_item)
        val neighborhood= createdView.findViewById<TextView>(R.id.market_neighborhood_item)
        val number = createdView.findViewById<TextView>(R.id.market_number_item)
        val street = createdView.findViewById<TextView>(R.id.market_street_item)

        name.text = mkt.name
        city.text = mkt.city
        neighborhood.text = mkt.neighborhood
        number.text = mkt.number
        street.text = mkt.street

        return createdView
    }

    fun update(items: List<Market>) {
        clear()
        addAll(items)
    }

    fun remove(item: Market) {
        markets.remove(item)
        notifyDataSetChanged()
    }

    private fun clear() {
        markets.clear()
    }

    private fun addAll(items: List<Market>) {
        markets.addAll(items)
        notifyDataSetChanged()
    }
}