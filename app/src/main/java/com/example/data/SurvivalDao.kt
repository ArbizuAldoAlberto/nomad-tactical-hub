package com.example.data

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    // Solidarity Posts (Persisted + Web of Trust)
    @Query("SELECT * FROM solidarity_posts ORDER BY timestamp DESC")
    fun getAllSolidarityPosts(): Flow<List<SolidarityPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolidarityPost(post: SolidarityPostEntity)

    @Update
    suspend fun updateSolidarityPost(post: SolidarityPostEntity)

    @Delete
    suspend fun deleteSolidarityPost(post: SolidarityPostEntity)

    // Tourniquet Timers (Critical Trauma Care)
    @Query("SELECT * FROM tourniquet_timers ORDER BY appliedAtEpochMillis DESC")
    fun getAllTourniquetTimers(): Flow<List<TourniquetTimerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTourniquetTimer(timer: TourniquetTimerEntity)

    @Update
    suspend fun updateTourniquetTimer(timer: TourniquetTimerEntity)

    @Delete
    suspend fun deleteTourniquetTimer(timer: TourniquetTimerEntity)
}

@Database(
    entities = [
        SurvivalWaypoint::class,
        InventoryItem::class,
        TransmissionRecord::class,
        SolidarityPostEntity::class,
        TourniquetTimerEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SurvivalDatabase : RoomDatabase() {
    abstract fun dao(): SurvivalDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `solidarity_posts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `authorCallsign` TEXT NOT NULL,
                        `message` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `region` TEXT NOT NULL,
                        `latitude` REAL NOT NULL DEFAULT -36.2333,
                        `longitude` REAL NOT NULL DEFAULT -61.1167,
                        `upvotes` INTEGER NOT NULL DEFAULT 1,
                        `signatureHex` TEXT NOT NULL DEFAULT '',
                        `verifiedWebOfTrust` INTEGER NOT NULL DEFAULT 1,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tourniquet_timers` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `patientTag` TEXT NOT NULL,
                        `limbLocation` TEXT NOT NULL,
                        `appliedAtEpochMillis` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL DEFAULT 1,
                        `notes` TEXT NOT NULL DEFAULT ''
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
