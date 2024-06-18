package br.com.marketdeal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Market
import br.com.marketdeal.utils.ImageLoader
import com.google.android.material.imageview.ShapeableImageView

class MarketAdapter(private val context: Context) : BaseAdapter() {
    private val markets = ArrayList<Market>()

    fun update(items: List<Market>) {
        clear()
        markets.addAll(items)
        notifyDataSetChanged()
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
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val createdView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_market, parent, false)
        val market = markets[position]

        val name = createdView.findViewById<TextView>(R.id.item_market_name)
        val city = createdView.findViewById<TextView>(R.id.item_market_city)
        val neighborhood = createdView.findViewById<TextView>(R.id.item_market_neighborhood)
        val number = createdView.findViewById<TextView>(R.id.item_market_number)
        val street = createdView.findViewById<TextView>(R.id.item_market_street)
        val image = createdView.findViewById<ShapeableImageView>(R.id.activity_market_image)
        val spinner = createdView.findViewById<ProgressBar>(R.id.item_market_spinner)

        name.text = market.name
        city.text = market.city
        neighborhood.text = market.neighborhood
        number.text = market.number
        street.text = market.street

        if (market.imageUrl != null) {
            ImageLoader.loadImage(context, market.imageUrl, image, spinner)
        } else {
            image.setImageResource(R.drawable.ic_empty_image)
        }

        return createdView
    }
}
