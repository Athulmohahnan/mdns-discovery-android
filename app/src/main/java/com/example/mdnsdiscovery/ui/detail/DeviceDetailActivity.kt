package com.example.mdnsdiscovery.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.data.remote.IpInfoService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var txtIp: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtOrg: TextView
    private lateinit var txtCarrier: TextView
    private lateinit var progress: ProgressBar
    private lateinit var root: View
    private lateinit var cardDetails: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device_detail)
        initViews()
        setWindowInsets()
        fetchIpDetails()
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

    private fun initViews() {
        cardDetails = findViewById(R.id.cardDetails)
        root = findViewById(R.id.detailRoot)
        txtIp = findViewById(R.id.txtPublicIp)
        txtLocation = findViewById(R.id.txtLocation)
        txtOrg = findViewById(R.id.txtOrg)
        txtCarrier = findViewById(R.id.txtCarrier)
        progress = findViewById(R.id.progress)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchIpDetails() {
        progress.visibility = View.VISIBLE
        cardDetails.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val ip = withContext(Dispatchers.IO) {
                    IpInfoService.getPublicIp()
                }

                val info = withContext(Dispatchers.IO) {
                    IpInfoService.getIpInfo(ip)
                }

                txtIp.text = getString(R.string.ip, ip)
                txtLocation.text =
                    getString(
                        R.string.location,
                        info.optString("city"),
                        info.optString("region"),
                        info.optString("country")
                    )
                txtOrg.text = getString(R.string.org, info.optString("org"))
                "Carrier: ${info.optString("org")}".also { txtCarrier.text = it }

            } catch (e: Exception) {
                cardDetails.visibility = View.VISIBLE
                progress.visibility = View.GONE
                txtIp.text = getString(R.string.failed_to_load_ip_info)
            } finally {
                cardDetails.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_DEVICE_NAME = "extra_device_name"
        const val EXTRA_DEVICE_IP = "extra_device_ip"
    }
}

