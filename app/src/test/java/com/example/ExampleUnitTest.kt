package com.example

import com.example.util.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test nuclear fallout decay and gamma HVL shielding`() {
        val decay7h = CrisisCalculators.calculateFalloutDecay(1000.0, 7.0)
        assertTrue("At 7 hours radiation should drop to ~10% (Rule of 7:10)", decay7h in 90.0..105.0)

        val hvlEarth = CrisisCalculators.calculateGammaHvlShielding(100.0, "Tierra", 9.1)
        assertEquals(50.0, hvlEarth.blockedPercent, 0.5)
        assertEquals(50.0, hvlEarth.residualDoseRate, 0.5)
    }

    @Test
    fun `test exact water chlorination by bleach concentration g per L`() {
        val res55 = CrisisCalculators.calculateWaterChlorination(20.0, bleachConcentrationGramsPerLiter = 55, isTurbidOrCold = false)
        assertEquals(2.0, res55.targetDoseMgPerLiter, 0.01)
        assertEquals(15, res55.dropsRequired)
        assertEquals(30, res55.waitMinutes)

        val res25Turbid = CrisisCalculators.calculateWaterChlorination(20.0, bleachConcentrationGramsPerLiter = 25, isTurbidOrCold = true)
        assertEquals(4.0, res25Turbid.targetDoseMgPerLiter, 0.01)
        assertEquals(64, res25Turbid.dropsRequired)
        assertEquals(60, res25Turbid.waitMinutes)
    }

    @Test
    fun `test medical calculators WHO ORS, Parkland burns, START triage and pediatric dose`() {
        val ors = CrisisCalculators.calculateWhoOrsFormula(1.0)
        assertEquals(2.6, ors.saltGrams, 0.01)
        assertEquals(13.5, ors.sugarGrams, 0.01)

        val burns = CrisisCalculators.calculateParklandBurns(weightKg = 75.0, tbsaPercent = 20.0)
        assertEquals(6000.0, burns.totalFluid24hMl, 0.1)
        assertEquals(3000.0, burns.first8hFluidMl, 0.1)

        val triageRed = CrisisCalculators.evaluateStartTriage(
            walks = false,
            breathingAfterAirway = true,
            respRatePerMin = 36,
            radialPulsePresent = true,
            capillaryRefillSec = 1.5,
            followsCommands = true
        )
        assertEquals("ROJO", triageRed.codeColor)

        val paracetamol = CrisisCalculators.calculateEmergencyMedDose("Paracetamol", 20.0, isPediatric = true)
        assertEquals(300.0, paracetamol.singleDoseMg, 0.1)
    }

    @Test
    fun `test RF dipole antenna, Fresnel zone, 12V solar sizing and celestial navigation`() {
        val dipole = CrisisCalculators.calculateDipoleAntenna(146.520)
        assertEquals(48.62, dipole.quarterWaveCm, 0.2)

        val fresnel = CrisisCalculators.calculateFresnelZone(distanceKm = 5.0, freqGhz = 0.915, isObstructedTerrain = true)
        assertTrue(fresnel.fresnelRadiusMeters > 10.0)
        assertTrue(fresnel.recommendedMastHeightMeters > fresnel.fresnelRadiusMeters)

        val solar = CrisisCalculators.calculateSolarOffGridSystem(dailyWh = 600.0, autonomyDays = 2, systemVoltage = 12, isLiFePO4 = true)
        assertEquals(125.0, solar.batteryBankAh, 0.5)

        val celestial = CrisisCalculators.calculateCelestialNavigation(latitude = -36.2333, longitude = -61.1167, dayOfYear = 172, localSolarHour = 12.0)
        assertTrue(celestial.hemisphereGuide.contains("Cruz del Sur"))
    }

    @Test
    fun `test AES-256-GCM, One-Time Pad, Vigenere, Web of Trust and Duress PIN`() {
        val secretMsg = "Refugio Alfa Bolivar Coordenadas -36.2333 -61.1167 Agua Segura"
        val key = "CLAVE-TACTICA-2026"

        val aesCipher = SurvivalCipher.encryptAesGcm(secretMsg, key)
        val aesPlain = SurvivalCipher.decryptAesGcm(aesCipher, key)
        assertEquals(secretMsg, aesPlain)

        val otpCipher = SurvivalCipher.encryptOneTimePad(secretMsg, key)
        val otpPlain = SurvivalCipher.decryptOneTimePad(otpCipher, key)
        assertEquals(secretMsg, otpPlain)

        val sig = SurvivalCipher.signWebOfTrustMessage("LU-NOMAD-01", secretMsg, key)
        assertTrue(SurvivalCipher.verifyWebOfTrustMessage("LU-NOMAD-01", secretMsg, sig, key))
        assertFalse(SurvivalCipher.verifyWebOfTrustMessage("LU-NOMAD-01", "Mensaje alterado", sig, key))

        assertEquals(PinAccessMode.NORMAL, SurvivalCipher.evaluatePinAccess("2026"))
        assertEquals(PinAccessMode.DURESS_COERCION, SurvivalCipher.evaluatePinAccess("9911"))
        assertEquals(PinAccessMode.DENIED, SurvivalCipher.evaluatePinAccess("0000"))
    }

    @Test
    fun `test NEPP binary packet encoding and Reed-Solomon parity FEC single-byte recovery`() {
        val rawFrame = NomadSignalEngine.encodeNeppPacket(
            type = NeppMessageType.SOS_DISTRESS,
            senderCallsign = "BOLIVAR-01",
            latitude = -36.2333,
            longitude = -61.1167,
            payloadText = "SOS AGUA Y MEDICO EN REFUGIO ALFA"
        )
        assertTrue("NEPP frame must be compact (<128 bytes)", rawFrame.size < 128)

        // Corrupt 1 byte inside the payload to simulate VHF radio static
        val corruptedFrame = rawFrame.copyOf()
        corruptedFrame[18] = (corruptedFrame[18].toInt() xor 0x5A).toByte()

        val recovered = NomadSignalEngine.decodeNeppPacket(corruptedFrame)
        assertNotNull("FEC must mathematically recover the corrupted byte", recovered)
        assertEquals(1, recovered!!.fecRepairedBytes)
        assertEquals(NeppMessageType.SOS_DISTRESS, recovered.type)
        assertEquals(-36.2333, recovered.latitude, 0.0001)
        assertEquals(-61.1167, recovered.longitude, 0.0001)
        assertEquals("SOS AGUA Y MEDICO EN REFUGIO ALFA", recovered.payloadText)
    }

    @Test
    fun `test closed-loop AFSK audio modulation and Goertzel DSP demodulation`() {
        val originalBytes = "NOMAD".toByteArray(Charsets.UTF_8)
        val pcm = NomadSignalEngine.generateAfskPacketPcm(originalBytes, baudRate = 100)
        assertTrue(pcm.isNotEmpty())

        val demodulatedBytes = NomadSignalEngine.demodulateAfskPacketPcm(pcm, expectedByteCount = originalBytes.size, baudRate = 100)
        assertArrayEquals(originalBytes, demodulatedBytes)

        val morsePcm = NomadSignalEngine.generateMorseAudioPcm("... --- ...", includeVoxPreamble = true)
        assertTrue(morsePcm.size > 44100)

        val sstvPcm = NomadSignalEngine.generateSstvTacticalBeaconPcm()
        assertTrue(sstvPcm.size > 44100)
    }

    @Test
    fun `test NAIR 4-Ring Adaptive Intelligence Router selection and Ring 0 citations`() {
        val (ringLowBat, _) = NomadIntelligenceRouter.selectOptimalRing(isOnline = true, batteryPercent = 12)
        assertEquals(NairRing.RING_0_DETERMINISTIC, ringLowBat)

        val (ringUsb, _) = NomadIntelligenceRouter.selectOptimalRing(isOnline = false, batteryPercent = 80, isUsbLocalServerActive = true)
        assertEquals(NairRing.RING_2_USB_ARK_LLM, ringUsb)

        val (ringCloud, _) = NomadIntelligenceRouter.selectOptimalRing(isOnline = true, batteryPercent = 85, isUsbLocalServerActive = false)
        assertEquals(NairRing.RING_3_CLOUD_MULTI_LLM, ringCloud)

        val cardBleed = NomadIntelligenceRouter.queryRing0Deterministic("herida con sangrado arterial y torniquete")
        assertTrue(cardBleed.matchedRuleTitle.contains("MARCH"))
        assertTrue(cardBleed.arkFolderCitation.contains("01_MEDICINA_TRAUMA_Y_TRIAJE"))
    }
}
