package com.example.mdnsdiscovery.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.data.local.db.AppDatabase
import com.example.mdnsdiscovery.mdns.MdnsDiscoveryManager
import com.example.mdnsdiscovery.ui.home.adapter.DeviceAdapter
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var rvDevices: RecyclerView
    private lateinit var adapter: DeviceAdapter
    private lateinit var root: View


    private lateinit var db: AppDatabase
    private lateinit var mdnsManager: MdnsDiscoveryManager
    private val deviceList = mutableListOf<DeviceUiModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        initViews()
        setWindowInsets()
        setAdapter()
        initDatabaseAndMdns()
        loadDevicesFromDb()
        startMdnsDiscovery()
    }

    private fun initDatabaseAndMdns() {
        db = AppDatabase.getInstance(this)
        mdnsManager = MdnsDiscoveryManager(this, db.deviceDao())
    }

    private fun setAdapter() {
        adapter = DeviceAdapter(emptyList())
        rvDevices.layoutManager = LinearLayoutManager(this)
        rvDevices.adapter = adapter
    }


    private fun initViews() {
        root = findViewById(R.id.homeRoot)
        rvDevices = findViewById(R.id.rvDevices)
    }

    private fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                systemBars.top + v.paddingTop,
                v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

    }

    private fun loadDevicesFromDb() {
        lifecycleScope.launch {
            val devices = db.deviceDao().getAllDevices()

            deviceList.clear()
            deviceList.addAll(
                devices.map {
                    DeviceUiModel(
                        name = it.deviceName,
                        ipAddress = it.ipAddress,
                        isOnline = it.isOnline
                    )
                }
            )
            adapter.updateData(deviceList)
        }
    }

    private fun startMdnsDiscovery() {
        for (i in deviceList.indices) {
            deviceList[i] = deviceList[i].copy(isOnline = false)
        }
        adapter.updateData(deviceList)

        lifecycleScope.launch(Dispatchers.IO) {
            db.deviceDao().markAllOffline()
        }

        mdnsManager.startDiscovery { entity ->
            runOnUiThread {

                val index = deviceList.indexOfFirst {
                    it.ipAddress == entity.ipAddress
                }

                if (index >= 0) {
                    // Device rediscovered → mark online
                    deviceList[index] =
                        deviceList[index].copy(isOnline = true)
                } else {
                    // New device discovered
                    deviceList.add(
                        DeviceUiModel(
                            name = entity.deviceName,
                            ipAddress = entity.ipAddress,
                            isOnline = true
                        )
                    )
                }

                adapter.updateData(deviceList)
            }
        }
    }
}

