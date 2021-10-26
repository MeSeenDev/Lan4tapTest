package ru.meseen.dev.lan4tap.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.meseen.dev.lan4tap.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val v = inflater.inflate(R.layout.main_fragment, container, false)
        v.findViewById<TextView>(R.id.start).setOnClickListener {
            viewModel.connect()
            viewModel.sale(643)
        }

        v.findViewById<TextView>(R.id.stop).setOnClickListener {

            viewModel.stop()
        }

        viewModel.notification.observe(viewLifecycleOwner) {
            v.findViewById<TextView>(R.id.logos).append("\n  $it")
        }

        return v
    }

    fun onSale() {
        viewModel.connect()
        viewModel.sale(644)
    }
}