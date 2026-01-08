package com.example.mdnsdiscovery.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.ui.home.adapter.DeviceAdapter
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel

class HomeActivity : AppCompatActivity() {

    private lateinit var rvDevices: RecyclerView
    private lateinit var adapter: DeviceAdapter
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        initViews()
        setWindowInsets()
        setAdapter()
        loadDummyDevices()
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

    private fun loadDummyDevices() {
        val devices = listOf(
            DeviceUiModel("Living Room TV", "192.168.1.10", true),
            DeviceUiModel("Office Printer", "192.168.1.12", false),
            DeviceUiModel("Bedroom Speaker", "192.168.1.15", true)
        )
        adapter.updateData(devices)
    }
}
