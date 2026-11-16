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
    val protocol: String, // "AES-256-GCM", "One-Time Pad", "Vigenère", "NEPP-AFSK-1200", "SSTV-Robot36", "Morse-800Hz"
    val isIncoming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "solidarity_posts")
data class SolidarityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorCallsign: String,
    val message: String,
    val category: String, // "Ayuda Colectiva", "Mensaje de Esperanza", "Alerta Sanitaria", "Conocimiento Compartido"
    val region: String,
    val latitude: Double = -36.2333,
    val longitude: Double = -61.1167,
    val upvotes: Int = 1,
    val signatureHex: String = "",
    val verifiedWebOfTrust: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tourniquet_timers")
data class TourniquetTimerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientTag: String,
    val limbLocation: String, // "Brazo Izq", "Brazo Der", "Muslo Izq", "Muslo Der"
    val appliedAtEpochMillis: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val notes: String = ""
)
