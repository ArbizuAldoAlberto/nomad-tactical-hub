package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waypoints")
data class SurvivalWaypoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "Refugio", "Pozo de Agua", "Suministros", "Punto de Extracción", "Alarma/Peligro"
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Mochila (BOB)", "Alimentos/Despensa", "Primeros Auxilios", "Herramientas/Comms"
    val quantity: Int,
    val isPacked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transmissions")
data class TransmissionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rawText: String,
    val cipherText: String,
    val cipherKey: String,
    val protocol: String, // "AES-Nomad", "One-Time Pad", "Morse Code Only"
    val isIncoming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
