package com.example.util

import kotlin.math.pow
import kotlin.math.sqrt

object CrisisCalculators {

    // --- NUCLEAR FALLOUT DECAY (Rule of 7:10) ---
    // R(t) = R(1) * t^(-1.2)
    // t is elapsed time in hours. R1 is reading at t = 1 hour.
    fun calculateFalloutDecay(r1: Double, hours: Double): Double {
        if (r1 <= 0.0 || hours <= 0.0) return 0.0
        return r1 * hours.pow(-1.2)
    }

    // Get table/list of points for fallout decay to display in a chart or tabular format
    fun getDecayTimeline(r1: Double): List<Pair<Int, Double>> {
        val hours = listOf(1, 2, 7, 24, 48, 96, 168, 336) // Standard intervals up to 2 weeks
        return hours.map { h -> h to calculateFalloutDecay(r1, h.toDouble()) }
    }

    // --- NUCLEAR BLAST WAVE & THERMAL RADIUS ---
    // Approximates the effects of a ground or air burst nuclear detonation
    // based on yield (W) in kilotons (KT) and distance (d) in kilometers
    data class BlastEffects(
        val overpressurePsi: Double,
        val hazardLevel: String,
        val hazardColor: String, // "Red", "Orange", "Yellow", "Green"
        val arrivalTimeSeconds: Double,
        val thermalRadCalCm2: Double,
        val burnSeverity: String,
        val empRadiusKm: Double
    )

    fun calculateBlastEffects(yieldKt: Double, distanceKm: Double): BlastEffects {
        if (yieldKt <= 0.0 || distanceKm <= 0.0) {
            return BlastEffects(0.0, "Distancia o potencia no válida", "Green", 0.0, 0.0, "Ninguno", 0.0)
        }

        // Empirical scale calculations for pressure overpressure
        // Overpressure delta_p (approximate fit)
        // scaling factor: distance scaled by cube root of yield (d / W^(1/3))
        val scaledDistance = distanceKm / yieldKt.pow(1.0 / 3.0)
        
        // Fit for shockwave overpressure in PSI
        val overpressure = if (scaledDistance > 0.0) {
            (14.7 / scaledDistance.pow(3)) + (6.0 / scaledDistance)
        } else {
            100.0
        }.coerceIn(0.0, 500.0)

        // Wave arrival time (speed of sound roughly 343 m/s -> ~3.0 seconds per km for ambient air)
        // Shockwave travels faster near the center, but standardizes down
        val arrivalTime = distanceKm * 2.91 * (1.0 + 1.0 / overpressure).coerceAtMost(3.0)

        // Thermal energy Q (approximate cal/cm2) = yieldKt * 50 / (distanceSq)
        val thermalEnergy = (yieldKt * 15.0) / (distanceKm.pow(2))

        val hazardLevel = when {
            overpressure > 12.0 -> "DESINTEGRACIÓN INMEDIATA (Zona Cero)"
            overpressure > 5.0 -> "DESTRUCCIÓN CRÍTICA (Estructuras de Hormigón colapsadas, 100% letal)"
            overpressure > 2.0 -> "DESTRUCCIÓN MODERADA (Grave riesgo de incendios y escombros, peligro mortal)"
            overpressure > 1.0 -> "PRESION LEVE (Ventanas rotas, cortes por proyectiles de vidrio)"
            else -> "ZONA DE SEGURIDAD FÍSICA (Atención al Fallout posterior)"
        }

        val hazardColor = when {
            overpressure > 5.0 -> "Red"
            overpressure > 2.0 -> "Orange"
            overpressure > 1.0 -> "Yellow"
            else -> "Green"
        }

        val burnSeverity = when {
            thermalEnergy > 10.0 -> "Quemaduras de 3er grado completas (Afecta tendones/huesos)"
            thermalEnergy > 5.0 -> "Quemaduras de 2º grado (Ampollas profundas, cicatriz durable)"
            thermalEnergy > 2.0 -> "Quemaduras de 1er grado (Eritema severo, similar a insolación extrema)"
            else -> "Sin consecuencias térmicas superficiales directas"
        }

        // EMP radius scales with line of sight, typically high-altitude electromagnetic pulse (HEMP)
        // For a generic surface/low air burst, direct high-intensity EMP is roughly localized to blast
        val empRadius = 1.8 * yieldKt.pow(0.4)

        return BlastEffects(
            overpressurePsi = overpressure,
            hazardLevel = hazardLevel,
            hazardColor = hazardColor,
            arrivalTimeSeconds = arrivalTime,
            thermalRadCalCm2 = thermalEnergy,
            burnSeverity = burnSeverity,
            empRadiusKm = empRadius
        )
    }

    // --- EMP SHIELDING ATTENUATION (Faraday Cage) ---
    // Approximates electromagnetic shielding attenuation (using simplified transmission formulas)
    // mm: thickness in millimetres
    // material: "Cobre", "Aluminio", "Acero", "Platino"
    data class ShieldingResult(
        val attenuationDb: Double,
        val energyBlockedPercent: Double,
        val safetyAssessment: String,
        val assessmentColor: String // "Green", "Yellow", "Red"
    )

    fun calculateEmpShielding(thicknessMm: Double, material: String): ShieldingResult {
        if (thicknessMm <= 0.0) {
            return ShieldingResult(0.0, 0.0, "Sin blindaje. Vulnerabilidad crítica e inmediata.", "Red")
        }

        // Relative electrical conductivity and permeability values
        val (conductivity, permeability) = when (material) {
            "Cobre" -> 1.00 to 1.0
            "Aluminio" -> 0.61 to 1.0
            "Acero" -> 0.10 to 100.0 // Steel has higher permeability, absorbing low-freq magnetic field
            "Níquel" -> 0.22 to 100.0
            else -> 0.50 to 1.0
        }

        // Simplified Skin Depth and shield efficiency at typical high frequency (EMP spans 500kHz - 1GHz)
        // High frequency absorption loss is calculated in dB as:
        var absorptionLoss = 131.4 * thicknessMm * sqrt(conductivity * permeability)
        
        // Minimum reflection loss (simplified average across frequencies)
        val reflectionLoss = 100.0 + 10.0 * conductivity

        val totalDb = (absorptionLoss + reflectionLoss).coerceIn(0.0, 240.0)
        
        // Percent of power blocked = 100 * (1 - 10^(-dB/10))
        val powerReductionRatio = 1.0 - 10.0.pow(-totalDb / 10.0)
        val percentBlocked = (powerReductionRatio * 100.0).coerceIn(0.0, 100.0)

        val assessment = when {
            totalDb >= 80.0 -> "MÁXIMO GRADO DEFENSIVO CIVIL (Apto para servidores y emisores cuánticos)"
            totalDb >= 40.0 -> "EXCELENTE PROTECCIÓN TÁCTICA (Dispositivos portátiles y radios protegidos)"
            totalDb >= 15.0 -> "PROTECCIÓN INTERMEDIA (Mitiga daño leve, pero propenso a inducción a largo plazo)"
            else -> "BLINDAJE DEFICIENTE (Riesgo severo de acoplamiento térmico electromagnético)"
        }

        val assessmentColor = when {
            totalDb >= 40.0 -> "Green"
            totalDb >= 15.0 -> "Yellow"
            else -> "Red"
        }

        return ShieldingResult(totalDb, percentBlocked, assessment, assessmentColor)
    }

    // --- WATER USAGE & DEHYDRATION PREDICTOR IN DROUGHTS ---
    // Predicts dehydration rate and daily water kebutuhan
    // tempC: current ambient temperature in degrees Celsius
    // activityLevel: "Resting", "Active Walking", "Extreme Sentry"
    // currentLitres: water supply in liters
    data class DehydrationStats(
        val waterNeededLitres: Double,
        val survivalHoursWithoutWater: Double,
        val daysWithSupply: Double,
        val statusImpact: String,
        val statusColor: String // "Green", "Yellow", "Orange", "Red"
    )

    fun calculateDehydration(tempC: Double, activityLevel: String, currentLitres: Double): DehydrationStats {
        // Base rate of water loss per day in litres for standard conditions
        var dailyLoss = 2.0

        // Increase loss due to temperature
        if (tempC > 25.0) {
            dailyLoss += (tempC - 25.0) * 0.12
        }
        if (tempC > 35.0) {
            dailyLoss += (tempC - 35.0) * 0.35 // sweat rate spikes in high heat
        }

        // Increase loss due to activity level
        dailyLoss += when (activityLevel) {
            "Caminata Activa" -> 2.5
            "Físico Extremo" -> 5.0
            else -> 0.0 // Reposo
        }

        val survivalDays = if (dailyLoss > 0.0) currentLitres / dailyLoss else 99.0

        // Survival without water altogether in hours:
        // A human can survive roughly losing 10-15% of body weight (approx 10 liters of water loss)
        // Total reserve before collapse is roughly 7-8 liters.
        val totalReserveCapacity = 8.0
        val survivalHours = (totalReserveCapacity / dailyLoss) * 24.0

        val (status, color) = when {
            survivalHours < 24.0 -> "VULNERABILIDAD EXTREMA: Deshidratación terminal e insolación inminente antes de 24h" to "Red"
            survivalHours < 48.0 -> "RIESGO SEVERO DE ALUCINACIONES Y SHOCK DE CALOR EN 48h" to "Orange"
            survivalHours < 120.0 -> "Racionamiento necesario para evitar fallo multiorgánico en 3 a 5 días" to "Yellow"
            else -> "Estable temporalmente. Conserve la humedad corporal." to "Green"
        }

        return DehydrationStats(
            waterNeededLitres = dailyLoss,
            survivalHoursWithoutWater = survivalHours,
            daysWithSupply = survivalDays,
            statusImpact = status,
            statusColor = color
        )
    }
}
