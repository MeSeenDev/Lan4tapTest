package ru.meseen.dev.lan4tap.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
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
        v.findViewById<TextView>(R.id.connect).setOnClickListener {
            Snackbar.make(it,"connect",Snackbar.LENGTH_LONG).show()
            viewModel.connect()
        }
        v.findViewById<TextView>(R.id.start).setOnClickListener {
            Snackbar.make(it,"sale",Snackbar.LENGTH_LONG).show()
            viewModel.testHost()
        }

        v.findViewById<TextView>(R.id.stop).setOnClickListener {
            Snackbar.make(it,"stop",Snackbar.LENGTH_LONG).show()
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