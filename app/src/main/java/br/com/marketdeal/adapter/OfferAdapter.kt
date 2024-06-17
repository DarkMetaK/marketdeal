package br.com.marketdeal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Offer

class OfferAdapter(private val context: Context) : BaseAdapter() {
    private val offers = ArrayList<Offer>()

    private fun addAll(items: List<Offer>) {
        offers.addAll(items)
        notifyDataSetChanged()
    }

    fun update(items: List<Offer>) {
        clear()
        addAll(items)
    }

    private fun clear() {
        offers.clear()
    }

    override fun getCount(): Int {
        return offers.size
    }

    override fun getItem(position: Int): Offer {
        return offers[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val createdView = LayoutInflater
            .from(context)
            .inflate(R.layout.item_offer, viewGroup, false)

        val offer = offers[position]

        val title = createdView.findViewById<TextView>(R.id.offer_item_title)
        val date = createdView.findViewById<TextView>(R.id.offer_item_date)
        val size = createdView.findViewById<TextView>(R.id.offer_item_size)
        val market = createdView.findViewById<TextView>(R.id.offer_item_market)
        val originalPrice = createdView.findViewById<TextView>(R.id.offer_item_original_price)
        val currentPrice = createdView.findViewById<TextView>(R.id.offer_item_current_price)

        title.text = offer.productName
        date.text = offer.createdAt
        size.text = offer.size
        market.text = offer.marketName
        originalPrice.text = "R$ " + offer.originalPrice
        currentPrice.text = "R$ " + offer.currentPrice

        return createdView
    }

}