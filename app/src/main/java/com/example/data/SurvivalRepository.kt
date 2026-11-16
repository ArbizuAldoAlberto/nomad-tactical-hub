package com.example.data

import com.example.util.SurvivalCipher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class SurvivalRepository(private val dao: SurvivalDao) {

    val allWaypoints: Flow<List<SurvivalWaypoint>> = dao.getAllWaypoints()
    val allInventoryItems: Flow<List<InventoryItem>> = dao.getAllInventory()
    val allTransmissions: Flow<List<TransmissionRecord>> = dao.getAllTransmissions()
    val allSolidarityPosts: Flow<List<SolidarityPostEntity>> = dao.getAllSolidarityPosts()
    val allTourniquetTimers: Flow<List<TourniquetTimerEntity>> = dao.getAllTourniquetTimers()

    suspend fun insertWaypoint(waypoint: SurvivalWaypoint) = dao.insertWaypoint(waypoint)
    suspend fun deleteWaypoint(waypoint: SurvivalWaypoint) = dao.deleteWaypoint(waypoint)

    suspend fun insertInventoryItem(item: InventoryItem) = dao.insertInventoryItem(item)
    suspend fun deleteInventoryItem(item: InventoryItem) = dao.deleteInventoryItem(item)
    suspend fun updateInventoryItem(item: InventoryItem) = dao.updateInventoryItem(item)

    suspend fun insertTransmission(record: TransmissionRecord) = dao.insertTransmission(record)
    suspend fun deleteTransmission(record: TransmissionRecord) = dao.deleteTransmission(record)

    suspend fun insertSolidarityPost(post: SolidarityPostEntity) = dao.insertSolidarityPost(post)
    suspend fun updateSolidarityPost(post: SolidarityPostEntity) = dao.updateSolidarityPost(post)
    suspend fun deleteSolidarityPost(post: SolidarityPostEntity) = dao.deleteSolidarityPost(post)

    suspend fun insertTourniquetTimer(timer: TourniquetTimerEntity) = dao.insertTourniquetTimer(timer)
    suspend fun updateTourniquetTimer(timer: TourniquetTimerEntity) = dao.updateTourniquetTimer(timer)
    suspend fun deleteTourniquetTimer(timer: TourniquetTimerEntity) = dao.deleteTourniquetTimer(timer)

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // Prepopulate Waypoints if empty (Centered on San Carlos de Bolívar, Bs. As., Argentina)
        val currentWaypoints = dao.getAllWaypoints().first()
        if (currentWaypoints.isEmpty()) {
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Búnker Refugio Alfa (Bolívar)",
                    type = "Refugio",
                    latitude = -36.2333,
                    longitude = -61.1167,
                    description = "Refugio seguro principal con despensa subterránea, banco solar 12V LiFePO4, NOMAD Ark USB y radio VHF 146.520 MHz."
                )
            )
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Pozo de Suministro Filtrado",
                    type = "Pozo de Agua",
                    latitude = -36.2200,
                    longitude = -61.1000,
                    description = "Fuente natural de agua subterránea. Requiere prefiltro textil + hipoclorito (2 mg/L) o filtro de membrana 0.1 micras."
                )
            )
            dao.insertWaypoint(
                SurvivalWaypoint(
                    name = "Caché de Suministros C1",
                    type = "Suministros",
                    latitude = -36.2450,
                    longitude = -61.1300,
                    description = "Contenedor estanco en jaula de Faraday. Contiene mantas Mylar, suero oral OMS, torniquetes CAT y transceptor LoRa."
                )
            )
        }

        // Prepopulate Inventory if empty
        val currentInventory = dao.getAllInventory().first()
        if (currentInventory.isEmpty()) {
            val initialItems = listOf(
                InventoryItem(name = "Manta Térmica de Supervivencia Mylar", category = "Mochila (BOB)", quantity = 4, isPacked = true),
                InventoryItem(name = "Filtro de Agua Portátil (0.1 micras)", category = "Mochila (BOB)", quantity = 2, isPacked = true),
                InventoryItem(name = "Linterna Solar Tactical LED (Dinamo)", category = "Mochila (BOB)", quantity = 1, isPacked = true),
                InventoryItem(name = "Pendrive Dual NOMAD Ark 128GB (exFAT + PAR2)", category = "Herramientas/Comms", quantity = 2, isPacked = true),
                InventoryItem(name = "Brújula Militar con Espejo Heliógrafo", category = "Herramientas/Comms", quantity = 1, isPacked = true),
                InventoryItem(name = "Radio Baofeng UV-5R + Cable AUX K1 AFSK/SSTV", category = "Herramientas/Comms", quantity = 2, isPacked = true),
                InventoryItem(name = "Nodo LoRa Meshtastic 915 MHz (ESP32 T-Beam)", category = "Herramientas/Comms", quantity = 2, isPacked = true),
                InventoryItem(name = "Raciones de Alta Densidad Calórica (2400 kcal)", category = "Alimentos/Despensa", quantity = 12, isPacked = true),
                InventoryItem(name = "Torniquete Táctico Militar CAT Gen 7", category = "Primeros Auxilios", quantity = 2, isPacked = true),
                InventoryItem(name = "Sobres de Sales de Rehidratación Oral (SRO OMS)", category = "Primeros Auxilios", quantity = 20, isPacked = true),
                InventoryItem(name = "Vendaje Hemostático e Israelí H-Bandage", category = "Primeros Auxilios", quantity = 3, isPacked = true)
            )
            for (item in initialItems) {
                dao.insertInventoryItem(item)
            }
        }

        // Prepopulate Solidarity Posts if empty
        val currentPosts = dao.getAllSolidarityPosts().first()
        if (currentPosts.isEmpty()) {
            val seedPosts = listOf(
                Triple(
                    "Nodo Bolívar Centro (LU-NOMAD-01)",
                    "Pozo de agua potable activo y servidor Wi-Fi Faro NOMAD Ark encendido en canal local. Traigan bidones y pendrives USB para copiar manuales médicos e INTA sin costo.",
                    "Ayuda Colectiva" to "Buenos Aires (Interior)"
                ),
                Triple(
                    "Nodo Radiofónico Biobío",
                    "Transmitiendo en VHF 146.520 MHz boletines médicos y mapas SSTV Robot36 cada 30 minutos. Mantengan escucha pasiva para ahorrar batería.",
                    "Conocimiento Compartido" to "Biobío / Cordillera"
                ),
                Triple(
                    "Red de Enlace Nomad (Mendoza)",
                    "Enlace LoRa 915 MHz operativo en alta montaña y repetidor acústico AFSK 1200 baud verificado. La empatía y la cooperación nos mantienen de pie.",
                    "Mensaje de Esperanza" to "Mendoza / Cuyo"
                ),
                Triple(
                    "Posta Sanitaria Solidaria",
                    "Médicos y enfermeros voluntarios atendiendo trauma leve, control de infecciones y entrega de sales de rehidratación oral OMS.",
                    "Alerta Sanitaria" to "Cono Sur"
                )
            )
            for ((author, msg, meta) in seedPosts) {
                val sig = SurvivalCipher.signWebOfTrustMessage(author, msg, "NOMAD-TRUST-KEY")
                dao.insertSolidarityPost(
                    SolidarityPostEntity(
                        authorCallsign = author,
                        message = msg,
                        category = meta.first,
                        region = meta.second,
                        latitude = -36.2333,
                        longitude = -61.1167,
                        upvotes = 25,
                        signatureHex = sig,
                        verifiedWebOfTrust = true
                    )
                )
            }
        }
    }

    /**
     * Generates a signed, SHA-256 verified .nomadpkg JSON payload for USB-C OTG Cold-Sync (Sneakernet).
     */
    suspend fun exportColdSyncPackage(nodeCallsign: String = "NOMAD-NODE-ALPHA", trustKey: String = "NOMAD-TRUST-KEY"): String = withContext(Dispatchers.IO) {
        val waypoints = dao.getAllWaypoints().first()
        val inventory = dao.getAllInventory().first()
        val posts = dao.getAllSolidarityPosts().first()

        val payloadObj = JSONObject()
        payloadObj.put("schema", "nomad.coldsync.v1")
        payloadObj.put("exportedAt", System.currentTimeMillis())
        payloadObj.put("nodeCallsign", nodeCallsign)

        val wpArray = JSONArray()
        for (wp in waypoints) {
            wpArray.put(JSONObject().apply {
                put("name", wp.name)
                put("type", wp.type)
                put("latitude", wp.latitude)
                put("longitude", wp.longitude)
                put("description", wp.description)
                put("timestamp", wp.timestamp)
            })
        }
        payloadObj.put("waypoints", wpArray)

        val invArray = JSONArray()
        for (item in inventory) {
            invArray.put(JSONObject().apply {
                put("name", item.name)
                put("category", item.category)
                put("quantity", item.quantity)
                put("isPacked", item.isPacked)
                put("timestamp", item.timestamp)
            })
        }
        payloadObj.put("inventory", invArray)

        val postsArray = JSONArray()
        for (post in posts) {
            postsArray.put(JSONObject().apply {
                put("authorCallsign", post.authorCallsign)
                put("message", post.message)
                put("category", post.category)
                put("region", post.region)
                put("latitude", post.latitude)
                put("longitude", post.longitude)
                put("upvotes", post.upvotes)
                put("signatureHex", post.signatureHex)
                put("timestamp", post.timestamp)
            })
        }
        payloadObj.put("solidarityPosts", postsArray)

        val canonicalData = payloadObj.toString()
        val sha256 = SurvivalCipher.sha256Hex(canonicalData)
        val signature = SurvivalCipher.signWebOfTrustMessage(nodeCallsign, sha256, trustKey)

        JSONObject().apply {
            put("packageFormat", "NOMAD_COLD_SYNC_PKG_V1")
            put("sha256", sha256)
            put("signatureEd25519Hmac", signature)
            put("payload", payloadObj)
        }.toString(2)
    }

    /**
     * Imports and merges a .nomadpkg JSON string using idempotent CRDT (LWW / Unique Key) rules.
     */
    suspend fun importColdSyncPackage(pkgJson: String, trustKey: String = "NOMAD-TRUST-KEY"): ColdSyncMergeResult = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(pkgJson)
            val payload = root.getJSONObject("payload")
            val expectedSha = root.optString("sha256", "")
            val actualSha = SurvivalCipher.sha256Hex(payload.toString())
            if (expectedSha.isNotBlank() && expectedSha != actualSha) {
                return@withContext ColdSyncMergeResult(false, 0, 0, 0, "Fallo de integridad SHA-256: El paquete .nomadpkg está corrupto o alterado.")
            }

            val existingWaypoints = dao.getAllWaypoints().first()
            val existingWpKeys = existingWaypoints.map { "${it.name}|${it.latitude}|${it.longitude}" }.toSet()
            var addedWaypoints = 0

            val wpArray = payload.optJSONArray("waypoints") ?: JSONArray()
            for (i in 0 until wpArray.length()) {
                val obj = wpArray.getJSONObject(i)
                val name = obj.optString("name", "Waypoint")
                val lat = obj.optDouble("latitude", -36.2333)
                val lng = obj.optDouble("longitude", -61.1167)
                val key = "$name|$lat|$lng"
                if (key !in existingWpKeys) {
                    dao.insertWaypoint(
                        SurvivalWaypoint(
                            name = name,
                            type = obj.optString("type", "Refugio"),
                            latitude = lat,
                            longitude = lng,
                            description = obj.optString("description", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                    addedWaypoints++
                }
            }

            val existingInv = dao.getAllInventory().first().associateBy { "${it.name}|${it.category}" }
            var mergedInventory = 0
            val invArray = payload.optJSONArray("inventory") ?: JSONArray()
            for (i in 0 until invArray.length()) {
                val obj = invArray.getJSONObject(i)
                val name = obj.optString("name", "Item")
                val cat = obj.optString("category", "Mochila (BOB)")
                val ts = obj.optLong("timestamp", 0L)
                val key = "$name|$cat"
                val current = existingInv[key]
                if (current == null) {
                    dao.insertInventoryItem(
                        InventoryItem(
                            name = name,
                            category = cat,
                            quantity = obj.optInt("quantity", 1),
                            isPacked = obj.optBoolean("isPacked", false),
                            timestamp = ts
                        )
                    )
                    mergedInventory++
                } else if (ts > current.timestamp) {
                    dao.updateInventoryItem(
                        current.copy(
                            quantity = obj.optInt("quantity", current.quantity),
                            isPacked = obj.optBoolean("isPacked", current.isPacked),
                            timestamp = ts
                        )
                    )
                    mergedInventory++
                }
            }

            val existingPosts = dao.getAllSolidarityPosts().first().map { "${it.authorCallsign}|${it.message}" }.toSet()
            var addedPosts = 0
            val postsArray = payload.optJSONArray("solidarityPosts") ?: JSONArray()
            for (i in 0 until postsArray.length()) {
                val obj = postsArray.getJSONObject(i)
                val author = obj.optString("authorCallsign", "Nodo")
                val msg = obj.optString("message", "")
                val sig = obj.optString("signatureHex", "")
                val key = "$author|$msg"
                if (msg.isNotBlank() && key !in existingPosts) {
                    val verified = SurvivalCipher.verifyWebOfTrustMessage(author, msg, sig, trustKey)
                    dao.insertSolidarityPost(
                        SolidarityPostEntity(
                            authorCallsign = author,
                            message = msg,
                            category = obj.optString("category", "Ayuda Colectiva"),
                            region = obj.optString("region", "Regional"),
                            latitude = obj.optDouble("latitude", -36.2333),
                            longitude = obj.optDouble("longitude", -61.1167),
                            upvotes = obj.optInt("upvotes", 1),
                            signatureHex = sig,
                            verifiedWebOfTrust = verified,
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                    addedPosts++
                }
            }

            ColdSyncMergeResult(
                success = true,
                waypointsAdded = addedWaypoints,
                inventoryMerged = mergedInventory,
                postsAdded = addedPosts,
                summary = "Sincronización en frío (.nomadpkg) completada: +$addedWaypoints waypoints, +$mergedInventory suministros, +$addedPosts boletines verificados."
            )
        } catch (e: Exception) {
            ColdSyncMergeResult(false, 0, 0, 0, "Error al importar paquete .nomadpkg: ${e.message}")
        }
    }
}

data class ColdSyncMergeResult(
    val success: Boolean,
    val waypointsAdded: Int,
    val inventoryMerged: Int,
    val postsAdded: Int,
    val summary: String
)
