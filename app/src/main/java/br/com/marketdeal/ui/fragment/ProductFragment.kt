package br.com.marketdeal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.ui.activity.ProductFormActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductFragment : Fragment() {
    private lateinit var productList: ListView
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        initializeFields(view)
        configFab()

        return view
    }

    private fun initializeFields(view: View) {
        productList = view.findViewById(R.id.fragment_product_list)
        fab = view.findViewById(R.id.fragment_product_fab)
    }

    private fun configFab() {
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), ProductFormActivity::class.java)
            startActivity(intent)
        }
    }

}