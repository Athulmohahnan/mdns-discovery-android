package com.example.mdnsdiscovery.mdns

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.mdnsdiscovery.data.local.db.DeviceDao
import com.example.mdnsdiscovery.data.local.db.DeviceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MdnsDiscoveryManager(
    context: Context,
    private val deviceDao: DeviceDao
) {

    private val nsdManager =
        context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private val scope = CoroutineScope(Dispatchers.IO)

    fun startDiscovery(onDeviceDiscovered: (DeviceEntity) -> Unit) {
        nsdManager.discoverServices(
            "_airplay._tcp.", // no smart devices use- using airplay for demo purposes
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener(onDeviceDiscovered)
        )
    }

    private fun discoveryListener(
        onDeviceDiscovered: (DeviceEntity) -> Unit
    ) = object : NsdManager.DiscoveryListener {

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            resolveService(serviceInfo, onDeviceDiscovered)
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.d("MDNS", "Service lost: ${serviceInfo.serviceName}")
        }

        override fun onDiscoveryStarted(regType: String) {
            Log.d("MDNS", "Discovery started for $regType")
        }
        override fun onDiscoveryStopped(serviceType: String) {
            Log.d("MDNS", "Discovery stopped for $serviceType")

        }
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
    }

    private fun resolveService(
        serviceInfo: NsdServiceInfo,
        onDeviceDiscovered: (DeviceEntity) -> Unit
    ) {
        nsdManager.resolveService(
            serviceInfo,
            object : NsdManager.ResolveListener {

                override fun onServiceResolved(resolved: NsdServiceInfo) {
                    val ip = resolved.host?.hostAddress ?: return

                    val device = DeviceEntity(
                        ipAddress = ip,
                        deviceName = resolved.serviceName,
                        isOnline = true,
                        lastSeen = System.currentTimeMillis()
                    )

                    scope.launch {
                        deviceDao.insertDevice(device)
                        onDeviceDiscovered(device)
                    }
                }

                override fun onResolveFailed(
                    serviceInfo: NsdServiceInfo,
                    errorCode: Int
                ) {
                    Log.e("MDNS", "Resolve failed $errorCode")
                }
            }
        )
    }
}
