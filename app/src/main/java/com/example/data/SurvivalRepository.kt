package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SurvivalRepository(private val dao: SurvivalDao) {

    val allWaypoints: Flow<List<SurvivalWaypoint>> = dao.getAllWaypoints()
    val allInventoryItems: Flow<List<InventoryItem>> = dao.getAllInventory()
    val allTransmissions: Flow<List<TransmissionRecord>> = dao.getAllTransmissions()

    suspend fun insertWaypoint(waypoint: SurvivalWaypoint) = dao.insertWaypoint(waypoint)
    suspend fun deleteWaypoint(waypoint: SurvivalWaypoint) = dao.deleteWaypoint(waypoint)

    suspend fun insertInventoryItem(item: InventoryItem) = dao.insertInventoryItem(item)
    suspend fun deleteInventoryItem(item: InventoryItem) = dao.deleteInventoryItem(item)
    suspend fun updateInventoryItem(item: InventoryItem) = dao.updateInventoryItem(item)

    suspend fun insertTransmission(record: TransmissionRecord) = dao.insertTransmission(record)
    suspend fun deleteTransmission(record: TransmissionRecord) = dao.deleteTransmission(record)

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // Prepopulate Waypoints if empty
        val currentWaypoints = dao.getAllWaypoints().first()
        if (currentWaypoints.isEmpty()) {
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Búnker Refugio Alfa",
                    type = "Refugio",
                    latitude = -36.2333,
                    longitude = -61.1167,
                    description = "Refugio seguro principal con despensa subterránea, equipo solar y radio VHF."
                )
            )
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Pozo de Suministro Filtrado",
                    type = "Pozo de Agua",
                    latitude = -36.2200,
                    longitude = -61.1000,
                    description = "Fuente natural de agua subterránea. Se requiere filtro de gravedad de 0.1 micras."
                )
            )
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Caché de Suministros C1",
                    type = "Suministros",
                    latitude = -36.2450,
                    longitude = -61.1300,
                    description = "Contenedor estanco enterrado. Contiene mantas térmicas, raciones secas y cartuchos."
                )
            )
        }

        // Prepopulate Inventory if empty
        val currentInventory = dao.getAllInventory().first()
        if (currentInventory.isEmpty()) {
            val initialItems = listOf(
                InventoryItem(name = "Manta Térmica de Supervivencia Mylar", category = "Mochila (BOB)", quantity = 4, isPacked = true),
                InventoryItem(name = "Filtro de Agua Portátil (Lifestraw)", category = "Mochila (BOB)", quantity = 2, isPacked = true),
                InventoryItem(name = "Linterna Solar Tactical LED (Dinamo)", category = "Mochila (BOB)", quantity = 1, isPacked = false),
                InventoryItem(name = "Brújula Militar Brunton con Espejo", category = "Herramientas/Comms", quantity = 1, isPacked = true),
                InventoryItem(name = "Radio Baofeng UV-5R Banda Dual", category = "Herramientas/Comms", quantity = 2, isPacked = false),
                InventoryItem(name = "Raciones MRE de Alta Caloría (Sabor Res)", category = "Alimentos/Despensa", quantity = 12, isPacked = true),
                InventoryItem(name = "Torniquete Táctico Militar CAT Gen 7", category = "Primeros Auxilios", quantity = 2, isPacked = true),
                InventoryItem(name = "Pastillas de Cloro Purificadoras (Aquatabs)", category = "Primeros Auxilios", quantity = 50, isPacked = false),
                InventoryItem(name = "Kit de Gasa y Vendaje Israelí H-Bandage", category = "Primeros Auxilios", quantity = 3, isPacked = true)
            )
            for (item in initialItems) {
                dao.insertInventoryItem(item)
            }
        }
    }
}
