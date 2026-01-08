package com.example.mdnsdiscovery.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel

class DeviceAdapter(
    private var devices: List<DeviceUiModel>
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvDeviceName)
        private val tvIp: TextView = itemView.findViewById(R.id.tvIp)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(device: DeviceUiModel) {
            tvName.text = device.name
            tvIp.text = device.ipAddress
            tvStatus.text = if (device.isOnline) itemView.context.getString(R.string.online) else itemView.context.getString(
                R.string.offline
            )
            tvStatus.setTextColor(
                if (device.isOnline) Color.GREEN else Color.RED
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size

    fun updateData(newDevices: List<DeviceUiModel>) {
        val diffCallback = DeviceDiffCallback(devices, newDevices)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        devices = newDevices
        diffResult.dispatchUpdatesTo(this)
    }
}
