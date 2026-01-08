package com.example.mdnsdiscovery.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val ipAddress: String,
    val deviceName: String,
    val isOnline: Boolean,
    val lastSeen: Long
)
