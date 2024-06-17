package br.com.marketdeal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Offer
import br.com.marketdeal.utils.ImageLoader
import com.google.android.material.imageview.ShapeableImageView

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

        val image = createdView.findViewById<ShapeableImageView>(R.id.item_offer_image)
        val spinner = createdView.findViewById<ProgressBar>(R.id.item_offer_spinner)
        val title = createdView.findViewById<TextView>(R.id.item_offer_title)
        val date = createdView.findViewById<TextView>(R.id.item_offer_date)
        val size = createdView.findViewById<TextView>(R.id.item_offer_size)
        val market = createdView.findViewById<TextView>(R.id.item_offer_market)
        val originalPrice = createdView.findViewById<TextView>(R.id.item_offer_original_price)
        val currentPrice = createdView.findViewById<TextView>(R.id.item_offer_current_price)

        if (offer.imageUrl != null) {
            ImageLoader.loadImage(context, offer.imageUrl, image, spinner)
        } else {
            image.setImageResource(R.drawable.ic_empty_image)
        }

        title.text = offer.productName
        date.text = offer.createdAt
        size.text = offer.size
        market.text = offer.marketName
        originalPrice.text = "R$ " + offer.originalPrice
        currentPrice.text = "R$ " + offer.currentPrice

        return createdView
    }

}