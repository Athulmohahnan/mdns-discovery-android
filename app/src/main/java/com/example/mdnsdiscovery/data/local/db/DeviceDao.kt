package com.example.mdnsdiscovery.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Query("UPDATE devices SET isOnline = 0")
    suspend fun markAllOffline()
}
