package com.example.mdnsdiscovery.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mdnsdiscovery.ui.home.models.DeviceUiModel

class DeviceDiffCallback(
    private val oldList: List<DeviceUiModel>,
    private val newList: List<DeviceUiModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].ipAddress == newList[newPos].ipAddress
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}
