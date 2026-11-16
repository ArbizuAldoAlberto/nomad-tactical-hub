# 🛰️ NOMAD Tactical Hub & Solidarity Network — MASTER BLUEPRINT (`v2.0.0-omega-ark`)
## Deep Developer Architecture, Off-Grid RF Protocol & Universal USB Ark Manual

This manual documents the production architecture of **NOMAD Tactical Hub & Solidarity Network (`v2.0.0-omega-ark`)**. Every subsystem originally proposed in Blueprints A, B, C, and D has been elevated to production across Android, Web PWA, `NOMAD Ark USB`, Analog/Digital Radio, and the 4-Ring Adaptive Intelligence Router (`NAIR`).

---

## 1. Pentadimensional Architectural Topology

```
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│                     VECTOR 1: JETPACK COMPOSE UI & TRAUMA / OLED MODES                   │
│  DashboardScreen  GuidesScreen  SolidarityScreen  CrisisScreen  MapScreen  CommsScreen   │
└────────────────────────────────────────────┬─────────────────────────────────────────────┘
                                             │ [StateFlow / Lifecycle-Aware Collectors]
                                             ▼
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│                        SurvivalViewModel (OMEGA Orchestrator)                            │
│  - Manages EMCON Silence, Duress PIN (9911), Trauma Mode, CPR 110 BPM & Cold-Sync       │
└──────┬──────────────────────────┬──────────────────────────────┬─────────────────────────┘
       │                          │                              │
       ▼ [Room WAL v2]            ▼ [360° Hardware Bridge]       ▼ [DSP, Crypto & 4-Ring AI]
┌──────────────────────┐  ┌──────────────────────────────┐  ┌──────────────────────────────┐
│  SurvivalRepository  │  │   HardwareSensorBridge.kt    │  │  - CrisisCalculators.kt (14) │
│  - waypoints         │  │  - Raw GNSS (LocationManager)│  │  - SurvivalCipher.kt (AES/OTP│
│  - inventory_items   │  │  - True Compass + EMF (µT)   │  │  - NomadSignalEngine.kt      │
│  - transmissions     │  │  - Barometer Storm Alert     │  │    (NEPP + AFSK + SSTV + FEC)│
│  - solidarity_posts  │  │  - Seismograph & Dead-Man SOS│  │  - NomadIntelligenceRouter   │
│  - tourniquet_timers │  │  - Camera Torch + Offline TTS│  │    (NAIR Rings 0, 1, 2 & 3)  │
└──────────┬───────────┘  └──────────────────────────────┘  └──────────────────────────────┘
           │
           ▼ [USB-C OTG Cold-Sync (.nomadpkg) & Wi-Fi Beacon (:8080)]
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│         VECTORS 2 & 3: NOMAD ARK USB (exFAT 00-07 + PAR2) & START_NOMAD_ARK.html         │
│  - Simultaneous .PDF + .EPUB (Kindle E-Ink) + .HTML (OLED/E-Ink) + .TXT + Pocket Cards   │
│  - SHA-256 Manifest + Reed-Solomon/ZIP .PAR2 Auto-Repair + START_COMMUNITY_BEACON.py     │
└──────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Room SQLite Database Schema `v2` (`WRITE_AHEAD_LOGGING`)

Persistence is managed by [`SurvivalDatabase`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/data/SurvivalDao.kt) in `WAL` mode with non-destructive migration `MIGRATION_1_2`:

1. **`waypoints` (`SurvivalWaypoint`):** Operational coordinates centered by default on **San Carlos de Bolívar, Bs. As., Argentina (`-36.2333, -61.1167`)**. Filtered automatically by `PinAccessMode.DURESS_COERCION` when unlocked with Duress PIN `9911`.
2. **`inventory_items` (`InventoryItem`):** Bug Out Bag (`BOB`), medical trauma supplies, water filtration, and radio hardware counters with CRDT timestamp resolution during `.nomadpkg` merges.
3. **`transmissions` (`TransmissionRecord`):** Encrypted log supporting `AES-256-GCM`, `One-Time Pad`, `Vigenère`, `NEPP-AFSK-1200`, `SSTV-Robot36`, and `Morse-800Hz`.
4. **`solidarity_posts` (`SolidarityPostEntity`):** Persistent community mutual-aid bulletin board with `signatureHex` (`HmacSHA256 / Ed25519 Web of Trust`) and `verifiedWebOfTrust` validation against spoofed coordinates.
5. **`tourniquet_timers` (`TourniquetTimerEntity`):** Tracks active limb tourniquets (`patientTag`, `limbLocation`, `appliedAtEpochMillis`) to prevent ischemic limb loss.

---

## 3. Binary Radio Frame Specification (`NEPP v1` + `FEC`)

Implemented in [`NomadSignalEngine.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/NomadSignalEngine.kt):
- **Header (`4 Bytes`):** Magic `0x4E 0x4D` (`"NM"`), Version `0x01`, Message Type (`SOS_DISTRESS`, `MEDICAL_TRIAGE`, `SHELTER_SAFEZONE`, `SOLIDARITY_AID`, `WAYPOINT_SYNC`).
- **Sender & Micro-Degree Coordinates (`12 Bytes`):** `senderHash` (`Int32`), `latMicro` (`Int32 = lat * 1e7`), `lngMicro` (`Int32 = lng * 1e7`).
- **Payload (`1 + 0..64 Bytes`):** Length byte + UTF-8/Encrypted message.
- **Integrity & Forward Error Correction (`18 Bytes`):** `CRC-16-CCITT` (`2 Bytes`) + `16-Byte` striped parity block capable of mathematically reconstructing corrupted bytes without retransmission.
- **Acoustic Modulation & Demodulation (`44.1 kHz PCM 16-bit`):**
  - `generateMorseAudioPcm()`: `800 Hz` sine wave with `300 ms` `1800 Hz` VOX opener preamble and `5 ms` *raised-cosine* ramp.
  - `generateAfskPacketPcm()` & `demodulateAfskPacketPcm()`: Bell 202 `1200 Hz` Mark / `2200 Hz` Space modulation and closed-loop `Goertzel` single-frequency energy demodulation.
  - `generateSstvTacticalBeaconPcm()`: `1900/1200/1900 Hz` VIS calibration header + `1500–2300 Hz` luminance scanlines.

---

## 4. Production Status of Architectural Blueprints A–D

| Blueprint | Original Scope | Current Status (`v2.0.0-omega-ark`) | Implementation File |
| :---: | :--- | :---: | :--- |
| **Blueprint A** | Physical GPS Hardware | ✅ **`[IMPLEMENTADO EN PRODUCCIÓN]`** | [`HardwareSensorBridge.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/HardwareSensorBridge.kt) (`LocationManager` GNSS + Compass + Barometer + Seismograph) |
| **Blueprint B** | Baofeng VHF Morse & Audio Modem | ✅ **`[IMPLEMENTADO EN PRODUCCIÓN]`** | [`NomadSignalEngine.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/NomadSignalEngine.kt) (`800Hz CW` + `AFSK 1200` + `SSTV` + `Goertzel RX`) |
| **Blueprint C** | Mesh Link & Cold-Sync | ✅ **`[IMPLEMENTADO EN PRODUCCIÓN]`** | [`SurvivalRepository.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/data/SurvivalRepository.kt) (`.nomadpkg` CRDT + `NEPP` LoRa/Audio frames) |
| **Blueprint D** | Local Wi-Fi Community Node (IIAB / Faro) | ✅ **`[IMPLEMENTADO EN PRODUCCIÓN]`** | [`START_COMMUNITY_BEACON.py`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/tools/nomad-ark-builder/START_COMMUNITY_BEACON.py) & [`START_NOMAD_ARK.html`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/web-ark/START_NOMAD_ARK.html) |
