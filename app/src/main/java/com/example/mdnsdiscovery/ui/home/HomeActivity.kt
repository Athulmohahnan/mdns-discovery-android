package com.example.mdnsdiscovery.ui.home

import android.content.Intent
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
import com.example.mdnsdiscovery.ui.detail.DeviceDetailActivity
import com.example.mdnsdiscovery.ui.home.adapter.DeviceAdapter
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var rvDevices: RecyclerView
    private lateinit var adapter: DeviceAdapter
    private lateinit var root: View
    private lateinit var db: AppDatabase
    private lateinit var mdnsManager: MdnsDiscoveryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        initViews()
        setWindowInsets()
        setAdapter()
        initDatabaseAndMdns()
        loadDevicesFromDb()
    }

    private fun initDatabaseAndMdns() {
        db = AppDatabase.getInstance(this)
        mdnsManager = MdnsDiscoveryManager(this, db.deviceDao())
    }

    private fun setAdapter() {
        adapter = DeviceAdapter { device ->
            val intent = Intent(this, DeviceDetailActivity::class.java)
            intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_NAME, device.name)
            intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_IP, device.ipAddress)
            startActivity(intent)
        }
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
            val devices = withContext(Dispatchers.IO) {
                db.deviceDao().getAllDevices()
            }

            val uiModels =  devices.map {
                    DeviceUiModel(
                        name = it.deviceName,
                        ipAddress = it.ipAddress,
                        isOnline = false
                    )
                }
            adapter.submitList(uiModels) {
                startMdnsDiscovery()
            }
        }
    }

    private fun startMdnsDiscovery() {
        lifecycleScope.launch(Dispatchers.IO) {
            db.deviceDao().markAllOffline()
        }

        mdnsManager.startDiscovery { entity ->
            runOnUiThread {
                val currentList = adapter.currentList.toMutableList()

                val index = currentList.indexOfFirst {
                    it.ipAddress == entity.ipAddress
                }

                if (index >= 0) {
                    currentList[index] =
                        currentList[index].copy(isOnline = true)
                } else {
                    // New device discovered
                    currentList.add(
                        DeviceUiModel(
                            name = entity.deviceName,
                            ipAddress = entity.ipAddress,
                            isOnline = true
                        )
                    )
                }

                adapter.submitList(currentList)
            }
        }
    }
}
