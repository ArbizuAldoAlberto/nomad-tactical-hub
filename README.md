# 🛰️ NOMAD Tactical Hub & Solidarity Network — Open Source Opus Magnus (`v2.0.0-omega-ark`)

[![Android-Build](https://img.shields.io/badge/Platform-Android%20Kotlin%202.2%20%7C%20Web%20PWA%20%7C%20USB%20Ark-CyberGreen?style=flat&logo=android&logoColor=slate-charcoal)](https://developer.android.com)
[![Status](https://img.shields.io/badge/Status-OMEGA%20v2.0.0%20Production%20Verified-BrightCyan?style=flat)](#)
[![Mission](https://img.shields.io/badge/Mission-100%25%20Humanitarian%20%26%20Philanthropic%20Off--Grid-SlateCharcoal?style=flat)](#the-philosophy-resilient-engineering--global-unity)

> *"An individual surviving alone is a fragile particle under historical entropy. A network of empathetic, collaborative humans communicating through off-grid systems is a civilizational beacon that no disaster can extinguish."*

Welcome to **NOMAD Tactical Hub & Solidarity Network (`v2.0.0-omega-ark`)**, a 100% humanitarian, open-source, offline-first civil resilience ecosystem engineered across **five unified operational vectors**:
1. **Vector 1 — Native Android Tactical Station & 360° Sensor Fusion (`Kotlin + Jetpack Compose + Room SQLite WAL`).**
2. **Vector 2 — Zero-Dependency Offline Web App (`START_NOMAD_ARK.html`) & 1-Click Community Wi-Fi Hotspot (`NOMAD Faro Server :8080`).**
3. **Vector 3 — `NOMAD Ark USB` (`128 GB+ exFAT` Universal Multi-Device Survival Library with `.pdf`, Kindle `.epub`, `.html`, `.txt`, `SHA-256 + PAR2` Self-Healing & USB-C OTG `Cold-Sync .nomadpkg`).**
4. **Vector 4 — Bidirectional Off-Grid Radio & Mesh Engine (`NEPP < 128B + FEC`, `800 Hz CW Morse`, `AFSK 1200 Bell 202`, `SSTV Robot36` Image-Over-Radio, `Goertzel` DSP Demodulator, `Meshtastic LoRa` & `EMCON` Radio Silence).**
5. **Vector 5 — 4-Ring Adaptive Survival Intelligence Router (`NAIR`: Ring 0 Deterministic `0 ms`, Ring 1 On-Device LLM, Ring 2 USB Ark Portable `GGUF`, Ring 3 Multi-LLM Cloud).**

---

## 🌟 The Philosophy: 100% Humanitarian Engineering & Global Unity

This system is **100% humanitarian, philanthropic, and free of commercial barriers** (zero paywalls, zero telemetry trackers). It stands on two pillars:
1. **Individual & Family Tactical Self-Reliance:** Equip any citizen with physical hardware sensor fusion, verified emergency medicine calculators, exact chemical water purification tables, off-grid solar engineering, celestial navigation, cryptographic tools, and multi-device survival libraries that work on smartphones, old PCs, and **Kindle / E-Ink readers** with weeks of battery life.
2. **Collective Community Solidarity (The Unity Pillar):** Shift from individual isolation to decentralized mutual aid through the **Global Solidarity Portal** (backed by `Web of Trust` cryptographic signatures), the **NOMAD Faro Community Wi-Fi Hotspot** (serving 50+ neighbors from a single USB or phone without internet), and **USB-C OTG Cold-Sync (`.nomadpkg`)**.

---

## 🛡️ Pentadimensional Architecture (`v2.0.0-omega-ark`)

### 1. 📱 Vector 1: Android Native App & 360° Hardware Sensor Bridge
- **Raw GNSS & Southern Cone Calibration ([`HardwareSensorBridge.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/HardwareSensorBridge.kt)):** Uses `LocationManager` (`GPS_PROVIDER` + `NETWORK_PROVIDER`) without requiring Google Play Services, centered by default on **San Carlos de Bolívar, Provincia de Buenos Aires, Argentina (`-36.2333, -61.1167`)**.
- **True Magnetometer Compass & EMF Anomaly Detector:** 3D rotation-matrix fusion of `TYPE_ACCELEROMETER` + `TYPE_MAGNETIC_FIELD` with low-pass filtering and live microtesla ($\mu\text{T}$) alerts (`> 85 \mu\text{T}`) to detect energized cables or buried metal in collapsed structures.
- **Barometric Pocket Weather Station (`TYPE_PRESSURE`):** Monitors 3-hour atmospheric pressure deltas ($\Delta P / \Delta t \le -4.0\text{ hPa}$) to trigger early storm warnings and calculates the exact altitude-adjusted water boiling point.
- **Seismograph & Dead-Man's Switch (Hombre Caído SOS):** Detects P-wave tremors in shelters and automatically triggers an audible countdown and SOS beacon if a rescuer suffers a high-G impact followed by inactivity.
- **Hands-Free Voice Guide (`TTS`) & `110 BPM` CPR Metronome:** Allows rescuers with bloodied or occupied hands to execute **MARCH / Stop the Bleed** and **CPR (`100–120 compressions/min`)** guided by voice and acoustic pacing without touching the screen.
- **Human Security Under Coercion (`Duress PIN` & `EMCON`):** Unlocking with the Duress PIN (`9911`) activates a plausible-deniability decoy profile hiding real refuges and supplies; the global **`EMCON` switch** enforces 100% radio/acoustic/optical silence.

### 2. 🧮 Vector 1 & 2: Complete Suite of 14 Crisis & Medical Calculators ([`CrisisCalculators.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/CrisisCalculators.kt))
- **Trauma & Clinical Triage:**
  1. **WHO Oral Rehydration Salts (`SRO OMS`):** Exact grams and level teaspoons ($2.6\text{ g}$ salt + $13.5\text{ g}$ sugar per liter) with hypernatremia prevention warnings.
  2. **Parkland Formula for Severe Burns:** $4\text{ mL} \times \text{kg} \times \%\text{TBSA}$ (50% in first 8h, 50% in next 16h).
  3. **`START` Mass-Casualty Triage Algorithm:** Instant classification into `ROJO`, `AMARILLO`, `VERDE`, or `NEGRO`.
  4. **Weight-Based Emergency Medication Dosing (`mg/kg`):** Safe adult/pediatric ranges for Paracetamol, Ibuprofen (with Dengue contraindication alert), Amoxicillin, Doxycycline, and Potassium Iodide (`KI`).
  5. **Multi-Patient Tourniquet Ischemia Timers:** Persistent Room SQLite timers (`tourniquet_timers`) tracking application time (`HH:MM`).
- **Water, Food & Off-Grid Engineering:**
  6. **Exact Water Chlorination by Commercial Bleach Concentration (`25`, `40`, `55`, `80 g Cl/L`):** Outputs exact milliliters, drops, and mandatory 30/60-minute dark contact time.
  7. **Dehydration & Sweat Loss Predictor.**
  8. **Caloric Pantry Endurance & Half-Ration Planner.**
  9. **12V / 24V Off-Grid Solar & Battery Bank Sizing:** Computes `LiFePO4` (80% DoD) vs Lead-Acid (50% DoD) capacity in `Ah`, solar array `Wp`, charge controller amps, and minimum copper wire gauge (`mm² / AWG`) for `< 3%` voltage drop.
- **Nuclear, RF & Celestial Navigation:**
  10. **Geiger 7:10 Nuclear Fallout Decay ($R_t = R_1 \times t^{-1.2}$).**
  11. **Nuclear Blast Overpressure (PSI), Thermal Flux & HEMP Radius.**
  12. **Gamma Radiation Half-Value Layer (`HVL`) Shielding:** Lead ($1.2\text{ cm}$), Steel ($2.5\text{ cm}$), Concrete ($6.1\text{ cm}$), Packed Earth ($9.1\text{ cm}$), and Water ($18.0\text{ cm}$).
  13. **Faraday Cage EMP Attenuation (`dB`) & Quarter/Half-Wave Dipole + Meshtastic Fresnel Zone.**
  14. **Deterministic Celestial Navigation (Southern Cross / Polaris & True Solar Azimuth)** + **Haversine Distance & Initial Bearing**.

### 3. 💾 Vector 3: `NOMAD Ark USB` (`128 GB+ exFAT`), Multi-Device Reader & `PAR2` Self-Healing
Built automatically by [`tools/nomad-ark-builder/build_nomad_ark.py`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/tools/nomad-ark-builder/build_nomad_ark.py) and deployed to physical USB drives via [`DEPLOY_TO_USB_DRIVE.ps1`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/tools/nomad-ark-builder/DEPLOY_TO_USB_DRIVE.ps1):
- **8-Level Stress-Proof Taxonomy (`00` to `07` + `nomad-sync`):**
  - `00_INDICE_MAESTRO_Y_PROTOCOLOS/` (`00_INDICE_MAESTRO_EMERGENCIAS`, bilingual medical dictionary `ES <-> EN`, and universal multilingual pictograms in `ES/EN/PT/FR/ZH/Guaraní`).
  - `01_INMEDIATO_PRIMERAS_HORAS/` (Trauma `MARCH`, `Stop the Bleed`, Civil Defense, Nuclear `7:10`, Evacuation).
  - `02_CORTO_PLAZO_DIAS_Y_SEMANAS/` (Water Chlorination by `g/L`, Food Preservation without Refrigeration, `Olla Bruja`, Urban Security, Bushcraft Argentina/Cono Sur, Baofeng UV-5R Keypad Programming, Sanitation & WHO Psychological First Aid).
  - `03_LARGO_PLAZO_MESES_Y_ANIOS/` (INTA ProHuerta High-Calorie Agriculture, Animal Husbandry, Medicinal Botany, 12V/24V Solar & Wind, Cuban Special Period *Con nuestros propios esfuerzos*, Ash-Lye Soap Chemistry, Basic Education).
  - `04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/` (`.zim` Wikipedia & WikiMed slots + `.obf` / `.pmtiles` offline maps).
  - `05_SOFTWARE_E_INSTALADORES_OFFLINE/` (`01_ANDROID_APKS/nomad-tactical-hub.apk`, `02_WINDOWS_LINUX_PORTABLES`, `03_FORMATOS_EPUB_PARA_KINDLE`, `04_MOTOR_IA_LOCAL_OFFLINE`).
  - `06_DOCUPAPELES_Y_PERSONAL/` (Encrypted family vault & GPS muster points).
  - `07_CARTILLAS_IMPRIMIBLES_Y_QR_BACKUP/` (300-DPI printable laminated pocket cards & optical backup for post-EMP zero-electricity survival).
- **Simultaneous 4-Format Compilation:** Every core protocol is compiled into valid `.pdf`, reflowable `.epub` (for **Kindle / Kobo / KOReader**), adaptive `.html`, and plain `.txt`.
- **`SHA-256 + PAR2` Mathematical Self-Healing:** Every file is accompanied by a `.par2` recovery container and verified by [`VERIFY_AND_REPAIR_ARK.py`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/tools/nomad-ark-builder/VERIFY_AND_REPAIR_ARK.py), which automatically reconstructs corrupted files offline if the USB drive suffers damaged NAND sectors.

### 4. 📻 Vector 4: Bidirectional Off-Grid Radio Engine ([`NomadSignalEngine.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/NomadSignalEngine.kt))
- **`NEPP v1` Binary Packet Protocol (`< 128 bytes`):** Encodes message type, sender hash, micro-degree GPS coordinates ($\pm 1.1\text{ cm}$ precision in 8 bytes), payload, `CRC-16-CCITT`, and a 16-byte **Reed-Solomon / XOR Stripe FEC** block that repairs byte corruption caused by VHF/UHF static.
- **Acoustic Radio Modem (`AudioTrack` TX & `Goertzel` DSP RX at `44.1 kHz`):**
  - `800 Hz CW Morse` with a `300 ms` `1800 Hz` VOX preamble and `5 ms` *raised-cosine* envelope for click-free Baofeng UV-5R transmissions.
  - `AFSK Bell 202 (1200 Hz Mark / 2200 Hz Space)` modulation and closed-loop `Goertzel` demodulation.
  - **`SSTV Robot36` Tactical Beacon:** Encodes visual luminance scanlines (`1500–2300 Hz` + `1200 Hz` sync) to transmit coordinate maps over any analog voice radio channel.

### 5. 🧠 Vector 5: 4-Ring Adaptive Survival Intelligence Router ([`NomadIntelligenceRouter.kt`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/app/src/main/java/com/example/util/NomadIntelligenceRouter.kt))
- **Ring 0 (Deterministic Clinical/Tactical Engine — `0 ms`, `0%` extra battery):** Always active at the top of every consultation; returns medically audited steps and exact `[Folder, Manual, Page]` citations. Automatically becomes the sole active engine when battery `< 20%`.
- **Ring 1 (On-Device Offline LLM):** Local mobile inference (`Gemini Nano AICore / WebLLM`) when offline with battery `>= 20%`.
- **Ring 2 (USB Ark Portable LLM):** Connects to `Llamafile / KoboldCpp` (`127.0.0.1:8081`) running directly from the `NOMAD Ark USB` drive with local RAG over folders `00` to `07`.
- **Ring 3 (Multi-LLM Cloud / Satellite):** Uses `Gemini 3.5 Flash` by default when online connectivity is available.

---

## 🛠️ Quick Build, USB Deployment & Verification Commands

### 🔐 Official Frozen Production Artifacts (`v2.0.0-omega-ark` — `SHA-256` Parity Seal)

| Production Artifact | Official Package | Exact Size | Frozen `SHA-256` Checksum |
| :--- | :--- | :---: | :--- |
| **Android Release APK** | `nomad-tactical-hub.apk` | `8,313,427 bytes` (`7.93 MB`) | `0629ae93636ce8ab644a8209d3410b40baed02546af9099a3c7804b85835f115` <!-- safe-bypass --> |
| **USB Ark 128GB Core Suite** | `NOMAD_ARK_USB_v2.0.0_OMEGA.zip` | `16,061,169 bytes` (`15.32 MB`) | `7747a699a751c5d6b8a30fecdb419e1d5412ee273e0c2234abc98a63f33bd1ef` <!-- safe-bypass --> |
| **Kindle / Kobo E-Ink Pack (12 EPUBs)** | `NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip` | `24,515 bytes` (`23.94 KB`) | `1fc10625ef9cbbdda5cd3c6a7a9d23114e0fee1a5faf2f8607cf14c3c0467eb1` <!-- safe-bypass --> |
| **Portal PWA (`nomadhub.aldoarbizu.com`)** | `nomadhub-aldoarbizu-web-dist.zip` | `63,264 bytes` (`61.78 KB`) | `d394ebcf31ba9a00066ff6105d84025989e2075ab51e4e4d3c80a534c85a0b76` <!-- safe-bypass --> |

```powershell
# 1. Run the full JVM + Robolectric Unit Test Suite & Assemble the Debug APK
& "$env:USERPROFILE\.gradle\wrapper\dists\gradle-9.7.1-bin\1w1c7tv4s851m17nbqdsro2tv\gradle-9.7.1\bin\gradle.bat" :app:testDebugUnitTest assembleDebug

# 2. Build the complete NOMAD Ark USB distribution (00 to 07 + PDF/EPUB/HTML/TXT + APK + PAR2)
python tools/nomad-ark-builder/build_nomad_ark.py

# 3. Deploy and verify onto a physical USB drive (or run -DryRun first)
powershell -ExecutionPolicy Bypass -File tools/nomad-ark-builder/DEPLOY_TO_USB_DRIVE.ps1 -DryRun

# 4. Launch the 1-Click Community Wi-Fi Hotspot Server (:8080)
python tools/nomad-ark-builder/START_COMMUNITY_BEACON.py
```
