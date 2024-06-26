package br.com.marketdeal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Product
import br.com.marketdeal.utils.ImageLoader
import com.google.android.material.imageview.ShapeableImageView

class ProductAdapter(private val context: Context) : BaseAdapter() {
    private val products = ArrayList<Product>()

    private fun addAll(items: List<Product>) {
        products.addAll(items)
        notifyDataSetChanged()
    }

    fun update(items: List<Product>) {
        clear()
        addAll(items)
    }

    private fun clear() {
        products.clear()
    }

    override fun getCount(): Int {
        return products.size
    }

    override fun getItem(position: Int): Product {
        return products[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val createdView = LayoutInflater
            .from(context)
            .inflate(R.layout.item_product, viewGroup, false)

        val product = products[position]

        val image = createdView.findViewById<ShapeableImageView>(R.id.item_product_image)
        val spinner = createdView.findViewById<ProgressBar>(R.id.item_product_spinner)
        val title = createdView.findViewById<TextView>(R.id.item_product_title)
        val producer = createdView.findViewById<TextView>(R.id.item_product_producer)
        val description = createdView.findViewById<TextView>(R.id.item_product_description)

        if (product.imageUrl != null) {
            ImageLoader.loadImage(context, product.imageUrl, image, spinner)
        } else {
            image.setImageResource(R.drawable.ic_empty_image)
        }

        title.text = product.name
        producer.text = product.producer
        description.text = product.description

        return createdView
    }

}