# 🛰️ NOMAD Tactical Hub & Solidarity Network — Open Source Opus Magnus

[![Android-Build](https://img.shields.io/badge/Platform-Android%20Jetpack%20Compose-CyberGreen?style=flat&logo=android&logoColor=slate-charcoal)](https://developer.android.com)
[![Status](https://img.shields.io/badge/Status-Optimized%20&%20Production%20Ready-BrightCyan?style=flat)](#)
[![Theme](https://img.shields.io/badge/Design-Cosmic%20Slate%20%26%20Cyber%20Green-SlateCharcoal?style=flat)](#visual-identity)

> *"An individual surviving alone is a fragile particle under historical entropy. A network of empathetic, collaborative humans communicating through off-grid systems is a civilizational beacon that no disaster can extinguish."*

Welcome to the **NOMAD Tactical Hub**, an advanced, offline-first tactical navigation, secure communication, crisis calculations, and humanitarian unity engine. Built natively for Android using **Kotlin**, **Jetpack Compose (Material 3)**, and **Room SQLite Database**, this system has been designed from the ground up as a resilient framework for global crisis management, communication, and mutual humanitarian aid.

---

## 🌟 The Philosophy: Resilient Engineering & Global Unity

Can an app help heal and protect the world? Yes—by serving as a **tactical blueprint for community cohesion**. This system stands on two robust pillars:

1. **Individual Tactical Self-Reliance:** Equip any citizen with offline-first maps, military-grade cryptographic tools, antenna calculations, shelter checklists, and specialized survival calculations (radio frequency wave calculations, radiation decay, food rations, explosive over-pressure).
2. **Collective Community Solidarity (The Unity Pillar):** Shift the paradigm from individual hoarding to decentralized mutual support. Through the **Global Solidarity Portal**, users commit to the **Cooperative Humanitarian Accord** (Pact of Empathy) and broadcast/listen to localized mutual aid requests, aligning with open-source infrastructure networks like **Kiwix**, **Internet-in-a-Box**, **Meshtastic LoRa**, and **SDR**.

---

## 🛡️ Core Capabilities & Subsystems

This software operates six core modules, easily accessible via a high-density tactical bottom navigation bar:

### 1. 🎛️ Tactical Control Board (Dashboard)
- Displays current **live-updating localized celestial context** (UTC/Local Time trackers, coordinate tracking, adaptive horizontal compass heading).
- High-contrast visual cards showing real-time, offline stats of your **Bug Out Bag (BOB)** inventory, tactical waypoints, and incoming decryptions.
- Quick-launch buttons to get critical tips on basic medicine, communication triage, and physical security.

### 2. 📖 Offline Survival Knowledge Base
- Complete search/category indexing of specialized handbooks on **Nuclear, Biological & Chemical (NBC) Protection**, **First Aid & Field Medicine**, **Antennas & RF Comms**, **Aquatic Purification**, and **Shelter Engineering**.
- Integrated with **AI Tactical Advisor** powered by the Gemini 3.5 Flash server-side REST API, giving real-time, highly intelligent scenario analyses for crises without relying on a browser or heavy apps.

### 3. 🤝 Global Solidarity Portal (Unidad)
- **The Cooperative Humanitarian Accord:** A 5-point moral binding pact on **Empatía**, **Apoyo**, **Información**, **Comunicaciones**, and **Vulnerables** to stand with humanity, defend the weak, and share resources.
- **Micro-Packet Mesh Bulletin Board:** Simulate LoRa packet transmissions or localized Wi-Fi bulletin boards where refuges request medical supplies, offer water/shelter, share open frequency data, and broadcast messages of hope.
- **The Skeleton-Matrix Ecosystem:** Displays details on deploying **Kiwix Offline Wiki Servers**, **Internet-in-a-Box**, **Meshtastic**, and **refugiOS forensic safeguards** to protect community databases.

### 4. 🧮 Crisis Calculators (Cálculos)
- **RF Antennas:** Analyzes wave frequency to extract the exact length of **Quarter-Wave** and **Half-Wave Dipole antennas** for custom hand-built radio transmitters.
- **Meshtastic & Fresnel RF Link:** Computes Fresnel Zone radius and recommended mast height over multi-km distances.
- **Nuclear Decay Calculator:** Calculates the safe decomposition rate of radiation over hours using the Geiger half-life formula.
- **Nuclear Blast Shockwave:** Analyzes mechanical overpressure (PSI), thermal wave arrival times (cal/cm²), and electromagnetic EMP radii.
- **Radiation Shielding:** Evaluates lead, concrete, steel, earth, or water thickness and measures energy block percentages.
- **Water Hydration Budget:** Measures water needed per day for communities, predicting total endurance and critical drought windows.
- **Food Ration Calculator:** Calculates total food days based on people counts and calorie inventories.

### 5. 📍 Offline Tactical Coordinates Ledger (GPS)
- Manages an offline database of **critical operational coordinates** (Water Sources, Refuges, Supply Drops, Extraction Points, Danger Zones).
- Instant waypoint registration with fallback to simulated current GPS coordinates.
- Fully compatible with standard double formats across standard decimal environments (automatic sanitization of `,` and `.` inputs).

### 6. 🔐 Cryptographic Cipher & MORSE Terminal (Comms)
- **Morse Code Engine:** Real-time text-to-code translator. Includes a **Led Beacon simulator** that blinks the emergency flashers/screen in perfect Morse intervals.
- **Survival Vigenère Cipher:** Encrypts tactical communications/coordination packets with custom secret phrases, compatible with manual paper-and-pencil operations if computers are compromised.

---

## 🎨 Visual Identity & Aesthetic Choices

The visual environment was built with **Zero Slop Aesthetic** and maximum high-contrast, eye-strain-friendly legibility under low-light or outdoor operations:

- **Slate Charcoal Canvas (`#0E1116`):** Provides a deep, velvety black background that maximizes battery standby times and mitigates operational light signature in dark zones.
- **Neon Cyber Green (`#00F29D` / `#00FF66`):** Promotes quick scanning and is the standard accent color for healthy status indicators, successful actions, and active networks.
- **Volt Bright Cyan (`#00E5FF`):** Used to demarcate radio data, cryptographic components, and technical measurements.
- **Alert Crimson Red (`#FF2E51` / `#FF1A43`):** Reserves high-contrast priority for alarms, critical deficits, radiation warnings, or danger zones.
- **Amber Warning Tone (`#FFB300`):** Used for non-lethal, high-importance telemetry anomalies or pending commitments.

---

## 🛠️ Developer Setup & Builds

The codebase uses modern Android engineering best practices with Gradle (Kotlin DSL), Jetpack Compose, Coroutines, and Room:

### Prerequisite Dependencies
All dependencies are centralized in the Gradle version catalog (`gradle/libs.versions.toml`). Dot-notation is mapped to Gradle blocks in `app/build.gradle.kts` automatically.

- **Kotlin:** 1.9.2x / Compose Compiler 1.5.8+
- **Jetpack Compose Architecture:** Material 3, Lifecycle, Navigation
- **Local Database:** Room SQLite with KSP Compiler
- **Asynchronous Flow:** Coroutines StateFlow & flow collector utilities

### Local Compilation
To compile the package and run it directly in your environment, perform:

```bash
# To compile and verify package assembly
gradle assembleDebug

# To execute the full local JVM Robolectric test suite (all CUJs)
gradle :app:testDebugUnitTest
```

### Secrets & API Key Management
To configure the server-side AI Tactical Advisor, do **NOT** modify or hardcode keys. The app relies on the **Secrets Gradle Plugin**:

1. Insert your Gemini API key in your workspace **Secrets Panel** under `GEMINI_API_KEY`.
2. Locally, place it in an `.env` file in the project's root:
   ```env
   GEMINI_API_KEY="AIzaSyYourKeyHere..."
   ```
3. Read the variable safely via `BuildConfig.GEMINI_API_KEY` inside Kotlin code.

---

## 🌍 Embracing the Global Matrix
We encourage developers to extend this codebase with hardware capabilities. See **`MASTER_BLUEPRINT.md`** inside this directory for full database schemas, source files descriptions, and specific extension vectors to integrate real VHF hardware (Baofeng, Android Audio Modems) or LoRa Mesh nodes.

---
*Created with the mission to unite communities, secure civilization memory, and demonstrate that software can be a genuine vessel of love and preservation for humanity.*
