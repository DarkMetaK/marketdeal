package br.com.marketdeal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import br.com.marketdeal.R
import br.com.marketdeal.ui.activity.MarketFormActivity
import br.com.marketdeal.ui.activity.SignInActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StoreFragment : Fragment(R.layout.fragment_store) {
    private lateinit var AddMarketBtn: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_store, container, false)

        initializeFields(view)
        AddMarketBtnConfig()

        return view
    }
    private fun initializeFields(view: View) {
        AddMarketBtn = view.findViewById(R.id.floatingAddMarket)
    }
    private fun AddMarketBtnConfig() {
        AddMarketBtn.setOnClickListener {
            val intent = Intent(requireActivity(), MarketFormActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}