package com.example.mdnsdiscovery.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mdnsdiscovery.R
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel

class DeviceAdapter(
    private val onItemClick: (DeviceUiModel) -> Unit
) : ListAdapter<DeviceUiModel, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvDeviceName)
        private val tvIp: TextView = itemView.findViewById(R.id.tvIp)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(device: DeviceUiModel) {
            tvName.text = device.name
            tvIp.text = device.ipAddress
            tvStatus.text = if (device.isOnline) "Online" else "Offline"
            tvStatus.setTextColor(if (device.isOnline) Color.GREEN else Color.RED)
            itemView.setOnClickListener { onItemClick(device) }
        }
    }
}
