package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurvivalDao {
    // Waypoints
    @Query("SELECT * FROM waypoints ORDER BY timestamp DESC")
    fun getAllWaypoints(): Flow<List<SurvivalWaypoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaypoint(waypoint: SurvivalWaypoint)

    @Delete
    suspend fun deleteWaypoint(waypoint: SurvivalWaypoint)

    // Inventory
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)

    @Update
    suspend fun updateInventoryItem(item: InventoryItem)

    // Transmissions
    @Query("SELECT * FROM transmissions ORDER BY timestamp DESC")
    fun getAllTransmissions(): Flow<List<TransmissionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransmission(record: TransmissionRecord)

    @Delete
    suspend fun deleteTransmission(record: TransmissionRecord)
}

@Database(
    entities = [
        SurvivalWaypoint::class,
        InventoryItem::class,
        TransmissionRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SurvivalDatabase : RoomDatabase() {
    abstract fun dao(): SurvivalDao
}
