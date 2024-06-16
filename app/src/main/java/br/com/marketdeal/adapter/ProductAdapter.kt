package br.com.marketdeal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.marketdeal.R
import br.com.marketdeal.model.Product
import br.com.marketdeal.utils.ImageLoader

class ProductAdapter(private val context: Context) : BaseAdapter() {
    private val products = ArrayList<Product>()

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
            .inflate(R.layout.product_item, viewGroup, false)

        val product = products[position]

        val image = createdView.findViewById<ImageView>(R.id.product_item_image)
        val spinner = createdView.findViewById<ProgressBar>(R.id.product_item_spinner)
        val title = createdView.findViewById<TextView>(R.id.product_item_title)
        val producer = createdView.findViewById<TextView>(R.id.product_item_producer)
        val description = createdView.findViewById<TextView>(R.id.product_item_description)

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


    fun update(items: List<Product>) {
        clear()
        addAll(items)
    }

    fun remove(item: Product) {
        products.remove(item)
        notifyDataSetChanged()
    }

    private fun clear() {
        products.clear()
    }

    private fun addAll(items: List<Product>) {
        products.addAll(items)
        notifyDataSetChanged()
    }
}