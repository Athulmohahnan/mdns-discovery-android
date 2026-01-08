package com.example.mdnsdiscovery.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel

class DeviceDiffCallback : DiffUtil.ItemCallback<DeviceUiModel>() {
    override fun areItemsTheSame(oldItem: DeviceUiModel, newItem: DeviceUiModel): Boolean {
        return oldItem.ipAddress == newItem.ipAddress
    }

    override fun areContentsTheSame(oldItem: DeviceUiModel, newItem: DeviceUiModel): Boolean {
        // Data class equality check
        return oldItem == newItem
    }
}
