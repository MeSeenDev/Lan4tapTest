package ru.meseen.dev.lan4tap.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.meseen.dev.lan4tap.R
import ru.meseen.dev.lan4tap.databinding.MainFragmentBinding


class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val vb by viewBinding(MainFragmentBinding::bind)

    private val vm: MainViewModel by viewModels();


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(vb) {
            startService.setOnClickListener {
                startService()
            }
            stopService.setOnClickListener {
                stopService()
            }
            restartService.setOnClickListener {
                restartService()
            }
            isRunning.setOnClickListener {
                Snackbar.make(it,"is Running ${isServiceRunning()}",Snackbar.LENGTH_LONG).show()
            }

            connect.setOnClickListener { vm.connect() }
            initial.setOnClickListener { vm.init() }
            testCommunication.setOnClickListener { vm.testCommunication() }
            testHost.setOnClickListener { vm.testHost() }
            selfTest.setOnClickListener { vm.selfTest() }
            sale.setOnClickListener { vm.sale(300) }
            settlement.setOnClickListener { vm.settlement() }
            stop.setOnClickListener { vm.stop() }
            logos.setOnClickListener { logos.text = "" }
        }

        vm.notification.observe(viewLifecycleOwner) {
            vb.logos.append("\n  $it")
        }
    }

    private fun startService() {
        val startServiceBroadcast = Intent("org.lanter.START_SERVICE")
        requireActivity().sendBroadcast(startServiceBroadcast)
    }

    private fun stopService() {
        val stopServiceBroadcast = Intent("org.lanter.STOP_SERVICE")
        requireActivity().applicationContext.sendBroadcast(stopServiceBroadcast)
    }

    private fun isServiceRunning(): Boolean {
        val manager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningServices(Int.MAX_VALUE).onEach { serviceInfo ->
            if (serviceInfo.service.className == "org.swcf.devices.HitsService") {
                return true;
            }
        }
        return false
    }


    private fun restartService() {
        lifecycleScope.launch {
            stopService()
            delay(500)
            startService()
        }
    }
}