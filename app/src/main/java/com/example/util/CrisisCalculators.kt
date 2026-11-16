package com.example.util

import kotlin.math.*

object CrisisCalculators {

    // --- 1. NUCLEAR FALLOUT DECAY (Rule of 7:10) ---
    // R(t) = R(1) * t^(-1.2)
    fun calculateFalloutDecay(r1: Double, hours: Double): Double {
        if (r1 <= 0.0 || hours <= 0.0) return 0.0
        return r1 * hours.pow(-1.2)
    }

    fun getDecayTimeline(r1: Double): List<Pair<Int, Double>> {
        val hours = listOf(1, 2, 7, 24, 48, 96, 168, 336) // Up to 2 weeks
        return hours.map { h -> h to calculateFalloutDecay(r1, h.toDouble()) }
    }

    // --- 2. NUCLEAR BLAST WAVE & THERMAL RADIUS ---
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

        val scaledDistance = distanceKm / yieldKt.pow(1.0 / 3.0)
        val overpressure = if (scaledDistance > 0.0) {
            (14.7 / scaledDistance.pow(3)) + (6.0 / scaledDistance)
        } else {
            100.0
        }.coerceIn(0.0, 500.0)

        val arrivalTime = distanceKm * 2.91 * (1.0 + 1.0 / overpressure).coerceAtMost(3.0)
        val thermalEnergy = (yieldKt * 15.0) / (distanceKm.pow(2))

        val hazardLevel = when {
            overpressure > 12.0 -> "DESINTEGRACIÓN INMEDIATA (Zona Cero)"
            overpressure > 5.0 -> "DESTRUCCIÓN CRÍTICA (Estructuras de Hormigón colapsadas, 100% letal)"
            overpressure > 2.0 -> "DESTRUCCIÓN MODERADA (Grave riesgo de incendios y escombros, peligro mortal)"
            overpressure > 1.0 -> "PRESIÓN LEVE (Ventanas rotas, cortes por proyectiles de vidrio)"
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

    // --- 3. EMP SHIELDING ATTENUATION (Faraday Cage) ---
    data class ShieldingResult(
        val attenuationDb: Double,
        val energyBlockedPercent: Double,
        val safetyAssessment: String,
        val assessmentColor: String
    )

    fun calculateEmpShielding(thicknessMm: Double, material: String): ShieldingResult {
        if (thicknessMm <= 0.0) {
            return ShieldingResult(0.0, 0.0, "Sin blindaje. Vulnerabilidad crítica e inmediata.", "Red")
        }

        val (conductivity, permeability) = when (material) {
            "Cobre" -> 1.00 to 1.0
            "Aluminio" -> 0.61 to 1.0
            "Acero" -> 0.10 to 100.0
            "Níquel" -> 0.22 to 100.0
            else -> 0.50 to 1.0
        }

        val absorptionLoss = 131.4 * thicknessMm * sqrt(conductivity * permeability)
        val reflectionLoss = 100.0 + 10.0 * conductivity
        val totalDb = (absorptionLoss + reflectionLoss).coerceIn(0.0, 240.0)

        val powerReductionRatio = 1.0 - 10.0.pow(-totalDb / 10.0)
        val percentBlocked = (powerReductionRatio * 100.0).coerceIn(0.0, 100.0)

        val assessment = when {
            totalDb >= 80.0 -> "MÁXIMO GRADO DEFENSIVO CIVIL (Apto para servidores y receptores SDR/VHF)"
            totalDb >= 40.0 -> "EXCELENTE PROTECCIÓN TÁCTICA (Dispositivos portátiles, NOMAD Ark USB y radios protegidos)"
            totalDb >= 15.0 -> "PROTECCIÓN INTERMEDIA (Mitiga daño leve, propenso a inducción cercana)"
            else -> "BLINDAJE DEFICIENTE (Riesgo severo de acoplamiento electromagnético)"
        }

        val assessmentColor = when {
            totalDb >= 40.0 -> "Green"
            totalDb >= 15.0 -> "Yellow"
            else -> "Red"
        }

        return ShieldingResult(totalDb, percentBlocked, assessment, assessmentColor)
    }

    // --- 4. GAMMA RADIATION SHIELDING (Half-Value Layer - HVL) ---
    data class GammaHvlResult(
        val hvlCm: Double,
        val transmissionRatio: Double,
        val blockedPercent: Double,
        val residualDoseRate: Double,
        val recommendation: String
    )

    fun calculateGammaHvlShielding(initialDoseRate: Double, material: String, thicknessCm: Double): GammaHvlResult {
        if (thicknessCm <= 0.0 || initialDoseRate < 0.0) {
            return GammaHvlResult(0.0, 1.0, 0.0, initialDoseRate.coerceAtLeast(0.0), "Sin blindaje másico interpuesto.")
        }
        val hvlCm = when (material.lowercase()) {
            "plomo", "lead" -> 1.2
            "acero", "steel" -> 2.5
            "hormigón", "hormigon", "concreto", "concrete" -> 6.1
            "tierra", "arena", "earth", "soil" -> 9.1
            "agua", "water" -> 18.0
            "madera", "wood" -> 29.0
            else -> 9.1
        }
        val ratio = 2.0.pow(-(thicknessCm / hvlCm))
        val blocked = ((1.0 - ratio) * 100.0).coerceIn(0.0, 100.0)
        val residual = initialDoseRate * ratio
        val rec = when {
            blocked >= 99.0 -> "Blindaje Nuclear Óptimo (>6.6 HVL): Reduce la radiación a menos del 1%."
            blocked >= 90.0 -> "Blindaje Sólido (>3.3 HVL): Reduce la dosis en un factor de 10x."
            blocked >= 50.0 -> "Blindaje Parcial: Añada más sacos de tierra o masa densa para superar 90%."
            else -> "Blindaje Insuficiente: Evacúe al sótano central o incremente espesor inmediatamente."
        }
        return GammaHvlResult(hvlCm, ratio, blocked, residual, rec)
    }

    // --- 5. WATER USAGE & DEHYDRATION PREDICTOR ---
    data class DehydrationStats(
        val waterNeededLitres: Double,
        val survivalHoursWithoutWater: Double,
        val daysWithSupply: Double,
        val statusImpact: String,
        val statusColor: String
    )

    fun calculateDehydration(tempC: Double, activityLevel: String, currentLitres: Double): DehydrationStats {
        var dailyLoss = 2.0
        if (tempC > 25.0) dailyLoss += (tempC - 25.0) * 0.12
        if (tempC > 35.0) dailyLoss += (tempC - 35.0) * 0.35

        dailyLoss += when (activityLevel) {
            "Caminata Activa" -> 2.5
            "Físico Extremo" -> 5.0
            else -> 0.0
        }

        val survivalDays = if (dailyLoss > 0.0) currentLitres / dailyLoss else 99.0
        val totalReserveCapacity = 8.0
        val survivalHours = (totalReserveCapacity / dailyLoss) * 24.0

        val (status, color) = when {
            survivalHours < 24.0 -> "VULNERABILIDAD EXTREMA: Deshidratación terminal e insolación inminente antes de 24h" to "Red"
            survivalHours < 48.0 -> "RIESGO SEVERO DE ALUCINACIONES Y SHOCK DE CALOR EN 48h" to "Orange"
            survivalHours < 120.0 -> "Racionamiento necesario para evitar fallo multiorgánico en 3 a 5 días" to "Yellow"
            else -> "Estable temporalmente. Conserve la humedad corporal." to "Green"
        }

        return DehydrationStats(dailyLoss, survivalHours, survivalDays, status, color)
    }

    // --- 6. RF DIPOLE ANTENNA & MESHTASTIC FRESNEL ZONE ---
    data class DipoleAntennaResult(
        val frequencyMhz: Double,
        val quarterWaveMeters: Double,
        val halfWaveMeters: Double,
        val quarterWaveCm: Double,
        val halfWaveCm: Double,
        val bandClassification: String
    )

    fun calculateDipoleAntenna(freqMhz: Double): DipoleAntennaResult {
        if (freqMhz <= 0.0) return DipoleAntennaResult(0.0, 0.0, 0.0, 0.0, 0.0, "Frecuencia inválida")
        val quarterM = 71.25 / freqMhz
        val halfM = 142.5 / freqMhz
        val band = when {
            freqMhz in 3.0..30.0 -> "HF (Onda Corta / Alcance Continental NVIS & DX)"
            freqMhz in 30.0..300.0 -> "VHF (Línea de Vista / 146.520 MHz Emergencia / Marina / NOAA)"
            freqMhz in 300.0..3000.0 -> "UHF (PMR446 / FRS / LoRa Meshtastic 433-915 MHz)"
            else -> "Banda Especial"
        }
        return DipoleAntennaResult(freqMhz, quarterM, halfM, quarterM * 100.0, halfM * 100.0, band)
    }

    data class FresnelZoneResult(
        val distanceKm: Double,
        val frequencyGhz: Double,
        val fresnelRadiusMeters: Double,
        val recommendedMastHeightMeters: Double
    )

    fun calculateFresnelZone(distanceKm: Double, freqGhz: Double = 0.915, isObstructedTerrain: Boolean = true): FresnelZoneResult {
        if (distanceKm <= 0.0 || freqGhz <= 0.0) return FresnelZoneResult(0.0, freqGhz, 0.0, 0.0)
        val radiusM = 17.32 * sqrt(distanceKm / (4.0 * freqGhz))
        val mastM = if (isObstructedTerrain) radiusM * 1.6 else radiusM
        return FresnelZoneResult(distanceKm, freqGhz, radiusM, mastM)
    }

    // --- 7. CALORIC RATIONS & PANTRY ENDURANCE ---
    data class CaloricRationResult(
        val daysOfSupply: Double,
        val dailyTotalCalories: Double,
        val halfRationDays: Double,
        val statusMessage: String
    )

    fun calculateCaloricRations(totalKcal: Double, people: Int, dailyKcalPerPerson: Double = 2000.0): CaloricRationResult {
        val safePeople = people.coerceAtLeast(1)
        val dailyTotal = safePeople * dailyKcalPerPerson.coerceAtLeast(500.0)
        val days = if (dailyTotal > 0.0) totalKcal.coerceAtLeast(0.0) / dailyTotal else 0.0
        val msg = when {
            days >= 30.0 -> "Autonomía Sólida (>30 días): Mantener rotación FIFO y control de humedad."
            days >= 14.0 -> "Autonomía de Cuarentena Nuclear (14+ días cubierta)."
            days >= 3.0 -> "Autonomía Básica de Evacuación (72h+). Priorizar recolección/trueque."
            else -> "Déficit Calórico Crítico (<72h). Activar racionamiento e hidratación con electrolitos."
        }
        return CaloricRationResult(days, dailyTotal, days * 2.0, msg)
    }

    // --- 8. EXACT WATER CHLORINATION BY COMMERCIAL BLEACH CONCENTRATION (g Cl/L) ---
    data class ChlorinationResult(
        val targetDoseMgPerLiter: Double,
        val mlOfBleachRequired: Double,
        val dropsRequired: Int,
        val waitMinutes: Int,
        val instructions: String
    )

    fun calculateWaterChlorination(
        liters: Double,
        bleachConcentrationGramsPerLiter: Int = 55,
        isTurbidOrCold: Boolean = false
    ): ChlorinationResult {
        if (liters <= 0.0 || bleachConcentrationGramsPerLiter <= 0) {
            return ChlorinationResult(0.0, 0.0, 0, 30, "Ingrese volumen de agua y concentración válida (25, 40, 55 u 80 g/L).")
        }
        val targetMgL = if (isTurbidOrCold) 4.0 else 2.0
        val mlRequired = (liters * targetMgL) / bleachConcentrationGramsPerLiter.toDouble()
        val drops = ceil(mlRequired * 20.0).toInt().coerceAtLeast(1)
        val waitMin = if (isTurbidOrCold) 60 else 30
        val instr = "Agregue $drops gotas (${String.format("%.2f", mlRequired)} mL) de hipoclorito sin aroma ($bleachConcentrationGramsPerLiter g/L) a ${String.format("%.1f", liters)} L de agua. Agite y repose $waitMin min en oscuridad. Debe percibirse ligero olor a cloro; si no, repita media dosis y espere 15 min."
        return ChlorinationResult(targetMgL, mlRequired, drops, waitMin, instr)
    }

    // --- 9. WHO ORAL REHYDRATION SALTS (SRO OMS) & PARKLAND BURNS & START TRIAGE ---
    data class WhoOrsResult(
        val liters: Double,
        val saltGrams: Double,
        val sugarGrams: Double,
        val saltLevelTeaspoons: Double,
        val sugarLevelTeaspoons: Double,
        val clinicalWarning: String
    )

    fun calculateWhoOrsFormula(liters: Double): WhoOrsResult {
        val safeLiters = liters.coerceAtLeast(0.0)
        val saltG = safeLiters * 2.6
        val sugarG = safeLiters * 13.5
        val saltTsp = safeLiters * 0.5
        val sugarTsp = safeLiters * 6.0
        return WhoOrsResult(
            liters = safeLiters,
            saltGrams = saltG,
            sugarGrams = sugarG,
            saltLevelTeaspoons = saltTsp,
            sugarLevelTeaspoons = sugarTsp,
            clinicalWarning = "CRÍTICO OMS: Use agua previamente hervida o clorada. NUNCA ponga más de 1/2 cucharadita rasa de sal (2.6 g) por litro; el exceso de sal provoca hipernatremia letal en niños."
        )
    }

    data class ParklandBurnResult(
        val totalFluid24hMl: Double,
        val first8hFluidMl: Double,
        val first8hRateMlPerHour: Double,
        val next16hFluidMl: Double,
        val next16hRateMlPerHour: Double,
        val clinicalNote: String
    )

    fun calculateParklandBurns(weightKg: Double, tbsaPercent: Double): ParklandBurnResult {
        if (weightKg <= 0.0 || tbsaPercent <= 0.0) {
            return ParklandBurnResult(0.0, 0.0, 0.0, 0.0, 0.0, "Ingrese peso en kg y % de Superficie Corporal Quemada (Regla de Wallace).")
        }
        val clampedTbsa = tbsaPercent.coerceIn(0.0, 100.0)
        val totalMl = 4.0 * weightKg * clampedTbsa
        val halfMl = totalMl / 2.0
        return ParklandBurnResult(
            totalFluid24hMl = totalMl,
            first8hFluidMl = halfMl,
            first8hRateMlPerHour = halfMl / 8.0,
            next16hFluidMl = halfMl,
            next16hRateMlPerHour = halfMl / 16.0,
            clinicalNote = "Fórmula de Parkland (Ringer Lactato): Administrar ${String.format("%.0f", halfMl)} mL en las primeras 8h desde la hora exacta del accidente (${String.format("%.0f", halfMl / 8.0)} mL/h) y ${String.format("%.0f", halfMl)} mL en las siguientes 16h (${String.format("%.0f", halfMl / 16.0)} mL/h). Vigilar diuresis (0.5-1 mL/kg/h)."
        )
    }

    data class StartTriageResult(
        val codeColor: String, // "VERDE", "AMARILLO", "ROJO", "NEGRO"
        val priorityTitle: String,
        val immediateAction: String
    )

    fun evaluateStartTriage(
        walks: Boolean,
        breathingAfterAirway: Boolean,
        respRatePerMin: Int,
        radialPulsePresent: Boolean,
        capillaryRefillSec: Double,
        followsCommands: Boolean
    ): StartTriageResult {
        if (walks) {
            return StartTriageResult("VERDE", "PRIORIDAD III — LEVE (Herido Ambulatorio)", "Dirigir a zona segura de observación secundaria. Reevaluar cada 30 min.")
        }
        if (!breathingAfterAirway) {
            return StartTriageResult("NEGRO", "EXPECTANTE / FALLECIDO (Apnea tras apertura de vía aérea)", "Tras realizar maniobra frente-mentón o tracción mandibular sin respiración espontánea en incidente masivo, priorizar pacientes ROJOS recuperables.")
        }
        if (respRatePerMin > 30 || respRatePerMin < 10) {
            return StartTriageResult("ROJO", "PRIORIDAD I — INMEDIATA (Compromiso Respiratorio Crítico: $respRatePerMin rpm)", "Asegurar vía aérea, descartar neumotórax a tensión, control de hemorragias MARCH y evacuación prioritaria #1.")
        }
        if (!radialPulsePresent || capillaryRefillSec > 2.0) {
            return StartTriageResult("ROJO", "PRIORIDAD I — INMEDIATA (Shock Hemorrágico / Hipoperfusión)", "Control inmediato de sangrado masivo (Torniquete / Empaquetado), abrigo térmico contra hipotermia y evacuación prioritaria #1.")
        }
        if (!followsCommands) {
            return StartTriageResult("ROJO", "PRIORIDAD I — INMEDIATA (Alteración Neurológica Aguda / TEC)", "Proteger columna cervical, posición lateral de seguridad si vomita y evacuación prioritaria #1.")
        }
        return StartTriageResult("AMARILLO", "PRIORIDAD II — DIFERIDA (Estable pero no ambulatorio)", "Inmovilizar fracturas, curar heridas, vigilar signos de shock cada 15 min y evacuar tras los pacientes ROJOS.")
    }

    data class MedDoseResult(
        val medicationName: String,
        val singleDoseMg: Double,
        val frequencyHours: Int,
        val maxDailyMg: Double,
        val guidanceAndWarnings: String
    )

    fun calculateEmergencyMedDose(medicationKey: String, weightKg: Double, isPediatric: Boolean): MedDoseResult {
        val safeWeight = weightKg.coerceIn(3.0, 150.0)
        return when (medicationKey.lowercase()) {
            "paracetamol", "acetaminofen" -> {
                val single = if (isPediatric || safeWeight < 50.0) (15.0 * safeWeight).coerceAtMost(1000.0) else 500.0
                val maxDay = (60.0 * safeWeight).coerceAtMost(4000.0)
                MedDoseResult("Paracetamol (Antipirético / Analgésico)", single, 6, maxDay, "Dosis: ${String.format("%.0f", single)} mg cada 6-8 h. MÁXIMO ABSOLUTO: ${String.format("%.0f", maxDay)} mg/24h (riesgo de fallo hepático fulminante). Fuente: Donde no hay doctor / OMS.")
            }
            "ibuprofeno" -> {
                val single = if (isPediatric || safeWeight < 40.0) (10.0 * safeWeight).coerceAtMost(400.0) else 400.0
                val maxDay = (40.0 * safeWeight).coerceAtMost(2400.0)
                MedDoseResult("Ibuprofeno (AINE / Antiinflamatorio)", single, 8, maxDay, "Dosis: ${String.format("%.0f", single)} mg cada 8 h con alimento. CONTRAINDICADO en sospecha de Dengue/Fiebres Hemorrágicas, deshidratación renal, úlcera o menores de 6 meses.")
            }
            "amoxicilina" -> {
                val dailyTotal = if (isPediatric || safeWeight < 40.0) (45.0 * safeWeight).coerceAtMost(1500.0) else 1500.0
                val single = dailyTotal / 3.0
                MedDoseResult("Amoxicilina (Antibiótico Beta-Lactámico)", single, 8, dailyTotal, "Dosis: ${String.format("%.0f", single)} mg cada 8 h por 7-10 días completos. VERIFICAR ALERGIA A PENICILINA antes de administrar.")
            }
            "doxiciclina" -> {
                val single = if (safeWeight < 45.0) 2.2 * safeWeight else 100.0
                MedDoseResult("Doxiciclina (Profilaxis Leptospirosis / Infecciones)", single, 12, single * 2.0, "Dosis: ${String.format("%.0f", single)} mg cada 12 h con abundante agua y sentado (evitar esofagitis). Precaución en embarazo y niños pequeños.")
            }
            else -> {
                MedDoseResult("Yoduro de Potasio (KI - Bloqueo Tiroideo Nuclear)", if (safeWeight >= 60.0) 130.0 else 65.0, 24, 130.0, "Administrar SOLO ante confirmación de lluvia radiactiva con Yodo-131 (130 mg adultos, 65 mg niños 3-18 años, 32 mg lactantes).")
            }
        }
    }

    // --- 10. OFF-GRID SOLAR 12V/24V & BATTERY BANK SIZING ---
    data class SolarSizingResult(
        val systemVoltage: Int,
        val batteryBankAh: Double,
        val solarArrayWattsPeak: Double,
        val chargeControllerAmps: Double,
        val minWireSectionMm2: Double,
        val recommendedAwg: String
    )

    fun calculateSolarOffGridSystem(
        dailyWh: Double,
        autonomyDays: Int = 2,
        systemVoltage: Int = 12,
        isLiFePO4: Boolean = true,
        peakSunHours: Double = 4.5,
        cableLengthMeters: Double = 5.0,
        maxLoadWatts: Double = 300.0
    ): SolarSizingResult {
        val v = if (systemVoltage == 24) 24.0 else 12.0
        val dod = if (isLiFePO4) 0.80 else 0.50 // 80% LiFePO4 vs 50% Lead-Acid
        val safeWh = dailyWh.coerceAtLeast(10.0)
        val totalStorageWh = (safeWh * autonomyDays.coerceAtLeast(1)) / dod
        val bankAh = totalStorageWh / v

        val solarWp = (safeWh * 1.30) / peakSunHours.coerceAtLeast(1.0) // 30% system loss margin
        val controllerAmps = (solarWp / v) * 1.25

        // Cable cross-section for <3% voltage drop: S = (2 * L * I) / (56 * 0.03 * V)
        val maxCurrentA = maxLoadWatts.coerceAtLeast(10.0) / v
        val maxDropV = v * 0.03
        val sectionMm2 = ((2.0 * cableLengthMeters.coerceAtLeast(1.0) * maxCurrentA) / (56.0 * maxDropV)).coerceAtLeast(2.5)
        val awg = when {
            sectionMm2 <= 2.5 -> "14 AWG (2.5 mm²)"
            sectionMm2 <= 4.0 -> "12 AWG (4.0 mm²)"
            sectionMm2 <= 6.0 -> "10 AWG (6.0 mm²)"
            sectionMm2 <= 10.0 -> "8 AWG (10 mm²)"
            sectionMm2 <= 16.0 -> "6 AWG (16 mm²)"
            else -> "4 AWG o superior (25+ mm²)"
        }
        return SolarSizingResult(v.toInt(), bankAh, solarWp, controllerAmps, sectionMm2, awg)
    }

    // --- 11. CELESTIAL NAVIGATION (Sun Azimuth & Southern Cross / Polaris) ---
    data class CelestialNavResult(
        val solarDeclinationDeg: Double,
        val solarElevationDeg: Double,
        val solarAzimuthDeg: Double,
        val dayLengthHours: Double,
        val hemisphereGuide: String
    )

    fun calculateCelestialNavigation(
        latitude: Double,
        longitude: Double,
        dayOfYear: Int,
        localSolarHour: Double
    ): CelestialNavResult {
        val d = dayOfYear.coerceIn(1, 365)
        // Cooper's equation for solar declination
        val declination = 23.45 * sin(Math.toRadians((360.0 / 365.0) * (284 + d)))
        val latRad = Math.toRadians(latitude.coerceIn(-89.0, 89.0))
        val decRad = Math.toRadians(declination)
        val hourAngleDeg = (localSolarHour - 12.0) * 15.0
        val hRad = Math.toRadians(hourAngleDeg)

        val sinElev = sin(latRad) * sin(decRad) + cos(latRad) * cos(decRad) * cos(hRad)
        val elevRad = asin(sinElev.coerceIn(-1.0, 1.0))
        val elevDeg = Math.toDegrees(elevRad)

        val cosAz = ((sin(decRad) - sin(latRad) * sin(elevRad)) / (cos(latRad) * cos(elevRad).coerceAtLeast(1e-6))).coerceIn(-1.0, 1.0)
        var azimuthDeg = Math.toDegrees(acos(cosAz))
        if (hourAngleDeg > 0) {
            azimuthDeg = 360.0 - azimuthDeg
        }

        val cosSunset = (-tan(latRad) * tan(decRad)).coerceIn(-1.0, 1.0)
        val dayHours = (2.0 / 15.0) * Math.toDegrees(acos(cosSunset))

        val guide = if (latitude < 0) {
            "Hemisferio Sur (Cruz del Sur): Prolongue 4.5 veces el eje mayor de la Cruz del Sur (de Gacrux a Acrux) en línea recta imaginaria y baje verticalmente al horizonte para hallar el SUR VERDADERO (180°). Al mediodía solar el Sol indica el NORTE."
        } else {
            "Hemisferio Norte (Estrella Polar): Ubique las dos estrellas punteras de la Osa Mayor (Merak y Dubhe), prolongue 5 veces su distancia para hallar POLARIS (NORTE VERDADERO 0°). Al mediodía solar el Sol indica el SUR."
        }
        return CelestialNavResult(declination, elevDeg, azimuthDeg, dayHours, guide)
    }

    // --- 12. BAROMETRIC WEATHER, ALTITUDE BOILING POINT & WIND CHILL ---
    data class BarometricWeatherResult(
        val estimatedAltitudeMeters: Double,
        val waterBoilingPointCelsius: Double,
        val pressureDelta3h: Double,
        val weatherForecast: String,
        val windChillCelsius: Double
    )

    fun calculateBarometricWeatherAndBoiling(
        pressureHpa: Double,
        pressure3hAgoHpa: Double,
        tempCelsius: Double,
        windKmh: Double
    ): BarometricWeatherResult {
        val safeP = pressureHpa.coerceIn(300.0, 1080.0)
        val altitudeM = 44330.0 * (1.0 - (safeP / 1013.25).pow(0.1903))
        val boilingC = (100.0 - (altitudeM * 0.00335)).coerceIn(70.0, 101.0)
        val delta = safeP - pressure3hAgoHpa

        val forecast = when {
            delta <= -6.0 -> "ALERTA ROJA METEOROLÓGICA: Caída barométrica violenta (${String.format("%.1f", delta)} hPa/3h). Tormenta severa, turbonada o frente destructivo inminente en <3 horas. Asegure refugio y desconecte antenas."
            delta <= -3.5 -> "ALERTA ÁMBAR: Presión en rápido descenso (${String.format("%.1f", delta)} hPa/3h). Lluvia y deterioro climático en las próximas 3 a 6 horas."
            delta >= 3.0 -> "Mejora Rápida (+${String.format("%.1f", delta)} hPa/3h): Ingreso de alta presión, despeje con posible viento fuerte y descenso térmico nocturno."
            else -> "Presión Estable (${String.format("%.1f", delta)} hPa/3h): Condiciones meteorológicas sin cambios bruscos inmediatos."
        }

        val windChill = if (tempCelsius <= 10.0 && windKmh >= 4.8) {
            val vPow = windKmh.pow(0.16)
            13.12 + 0.6215 * tempCelsius - 11.37 * vPow + 0.3965 * tempCelsius * vPow
        } else {
            tempCelsius
        }

        return BarometricWeatherResult(altitudeM, boilingC, delta, forecast, windChill)
    }

    // --- 13. HAVERSINE DISTANCE & INITIAL BEARING BETWEEN WAYPOINTS ---
    fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun calculateInitialBearingDeg(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val dLambda = Math.toRadians(lon2 - lon1)
        val y = sin(dLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLambda)
        return (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
    }
}
