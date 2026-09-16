# 🛰️ NOMAD Tactical Hub & Solidarity Network — MASTER BLUEPRINT
## Deep Developer Architecture & Offline Extension Manual

This manual provides an in-depth technological breakdown of the **NOMAD Tactical Hub** codebase. It outlines the architectural patterns, database schemas, cryptographic systems, physics models, and engineering maps required to scale or harden this application into a true post-crisis off-grid infrastructure node.

---

## 1. Architectural Topology & Reactive State flow

The system closely obeys **Clean Architecture** and **MVVM (Model-View-ViewModel)** paradigms to enforce strict separation of duties, ensuring maximum codebase reliability, state predictability, and offline execution safety.

```
       ┌────────────────────────────────────────────────────────────┐
       │                 JETPACK COMPOSE UI LAYER                   │
       │                                                            │
       │  DashboardScreen   GuidesScreen   SolidarityScreen         │
       │  CrisisScreen      MapScreen      TransmissionsScreen      │
       └──────────────────────────────┬─────────────────────────────┘
                                      │
                                      ▼ [Flows & States (e.g. StateFlow)]
       ┌────────────────────────────────────────────────────────────┐
       │                     SurvivalViewModel                      │
       │                                                            │
       │  - Orchestrates UI interactions and triggers coroutines    │
       │  - Manages active state properties and Morse thread loops   │
       │  - Collects Room db flows safely                           │
       └──────────────────┬────────────────────────────┬────────────┘
                          │                            │
                          ▼                            ▼ [Direct Calculation Calls]
    ┌─────────────────────────────┐        ┌───────────────────────────────────┐
    │     SurvivalRepository      │        │       UTILITY ENGINES (/util)     │
    │                             │        │                                   │
    │  Exposes flowing DB queries │        │  - CrisisCalculators.kt (Physics) │
    │  to ViewModel via IO thread │        │  - SurvivalCipher.kt (Cryptcom)   │
    │  isolation context.         │        │  - SurvivalKnowledgeBase (Kb)    │
    └─────────────┬───────────────┘        │  - GeminiClient.kt (REST AI)      │
                  │                        └───────────────────────────────────┘
                  ▼
    ┌─────────────────────────────┐
    │     SurvivalDatabase        │ (Offline Local Persistent Store)
    │                             │
    │   - waypoints / waypointDao │
    │   - inventory_items         │
    │   - transmissions           │
    └─────────────────────────────┘
```

### Flow Mechanics
- All database accesses are routed through the `SurvivalRepository` on non-blocking IO threads (`withContext(Dispatchers.IO)`), eliminating database freezes on standard single-thread microprocessors.
- Component state variables (e.g., text inputs, slider metrics, coordinates) are managed as `MutableStateFlow` streams inside `SurvivalViewModel.kt`. The UI layer collects these streams in a lifecycle-aware manner via `collectAsStateWithLifecycle()`.

---

## 2. SQLite Database Schema Deep Dive (Room)

Persistence is accomplished via **Android Room SQLite**, facilitating fast reads, robust indexes, and local offline state preservation when cell network hardware is completely down.

### Table: `waypoints`
Stores localized coordinates and strategic positions of refuges, hydration sources, and safezones.
- **`id`**: `Int` (Primary Key, Auto-Increment)
- **`name`**: `String` (Identify waypoint, e.g., "Refugio Cordillera")
- **`type`**: `String` (Enforced categorization: `"Refugio"`, `"Pozo de Agua"`, `"Suministros"`, `"Punto de Extracción"`, `"Alarma/Peligro"`)
- **`latitude`**: `Double` (Decolorized coordinates, e.g., `-33.456` for Southern Hemisphere compatibility)
- **`longitude`**: `Double` (Decolorized coordinates, e.g., `-70.648`)
- **`description`**: `String` (Tactical instructions, e.g., "Ingresar por la puerta trasera usando cuerda de escalada.")
- **`timestamp`**: `Long` (Epoch millisecond indexation)

### Table: `inventory_items`
Manages inventories belonging to search-and-rescue teams or community pantries.
- **`id`**: `Int` (Primary Key, Auto-Increment)
- **`name`**: `String` (Item descriptor, e.g., "Antibiotic Amoxicillin")
- **`category`**: `String` (`"Mochila (BOB)"`, `"Alimentos/Despensa"`, `"Primeros Auxilios"`, `"Herramientas/Comms"`)
- **`quantity`**: `Int` (Stock level counters)
- **`isPacked`**: `Boolean` (Identifies readiness for immediate relocation)
- **`timestamp`**: `Long`

### Table: `transmissions`
Maintains an encrypted log of all tactical communications processed.
- **`id`**: `Int` (Primary Key, Auto-Increment)
- ** rawText`**: `String` (Readable decrypted/original content)
- **`cipherText`**: `String` (Base64 cipher text / Morse sequence)
- **`cipherKey`**: `String` (Key signature token used)
- **`protocol`**: `String` (`"AES-Nomad"`, `"One-Time Pad"`, `"Morse Code Only"`)
- **`isIncoming`**: `Boolean` (Differentiates received signals from locally dispatched packets)
- **`timestamp`**: `Long`

---

## 3. Core Modules & Code Mapping

### `MainActivity.kt`
Initializes standard core subsystems. It sets up the `SurvivalDatabase` under the specific database name `"survival_hub_database"`. Under `onCreate`, it sets up standard destructive flight migrations and wraps the standard `SurvivalRepository` injection inside a custom nested `ViewModelProvider.Factory` instantiation.

### `SurvivalViewModel.kt`
The central hub for reactive operations. Key functions of note:
- **`prepopulateIfEmpty()`**: Dispatched during init block over Coroutines launch, filling initial sample water and base nodes.
- **`askAdvisor()`**: Serializes the `advisorPrompt` to JSON, triggers `GeminiClient.askGemini`, and handles standard success states or fallback logs.
- **`triggerMorseFlash()`**: Spins a recursive coroutine thread loop shifting a boolean `morseLightOn` according to Morse dot-and-dash durations. Developers can easily link this boolean to the native Camera API to blink the real flash bulb.

### `SurvivalHubScreen.kt`
A comprehensive layout compiling standard Jetpack Compose composables:
- **`DashboardScreen`**: Main grid visualizer. Renders current celestial data maps, battery estimations, and quick navigation routes.
- **`GuidesScreen`**: Scrollable indexer showing detailed local text arrays on water purification, medicine checklists, and radio antennas.
- **`SolidarityScreen`**: Houses the **Humanity Unification Portal**, cooperative pact status flags, community publishing cards, and links to the decentralized tech ecosystems.
- **`CrisisCalculatorScreen`**: Mathematical input boards tracking multiple specialized calculations.
- **`MapScreen`**: Simulates specialized coordinate tables, compasses, and offline waypoint tracking.
- **`TransmissionsScreen`**: Provides Vigenère cryptographic encryption grids and Morse audio-visual flashing consoles.

---

## 4. Engineering Mathematical Models (/util/CrisisCalculators.kt)

For severe survival scenarios, guessing is fatal. The `CrisisCalculators.kt` file consolidates highly optimized equations based on real physics and engineering parameters:

### Antenna Quarter-Wave Dipole Size Check
Based on radio propagation velocities, computes physical copper rod length for handheld antennas:
$$\text{Quarter Wave (meters)} = \frac{71.25}{\text{Frequency (MHz)}}$$
$$\text{Half Wave (meters)} = \frac{142.5}{\text{Frequency (MHz)}}$$

### Nuclear Decay Tracker (Geiger Decay)
Computes radioactive particle decay rates across hourly intervals:
$$R_t = R_0 \times t^{-1.2}$$
This models standard nuclear fallout decay based on the Rule of Sevens (every 7x increase in time reduces fallout intensity by 10x).

### Nuclear Blast Shockwave Propagation
Computes air overpressure, wave arrival speed, and EMP electromagnetic radius boundaries:
- **Overpressure Peak:**
  $$\text{Overpressure (PSI)} = \frac{330}{\text{Distance (km)}^3} + \frac{105}{\text{Distance (km)}}$$
- **Thermal Fireball Flux:**
  $$\text{Thermal Intensity (cal/cm²)} = \frac{Yield}{\alpha \times 4\pi R^2}$$

### Lead & Concrete Active Radiation Radiation Shielding
Tracks the attenuation coefficient (Half-Value Layer thickness, HVL) of different materials:
$$\text{Transmission Ratio} = 2^{-\left(\frac{\text{Thickness}}{\text{HVL}}\right)}$$
$$\text{Energy Blocked (\%)} = (1 - \text{Transmission Ratio}) \times 100$$

---

## 5. Architectural Extension Roadmap

For developers looking to adapt this application into a true post-crisis mesh tool, follow these specific integration blueprints:

### Blueprint A: Connecting Physical GPS Hardware
The current `MapScreen` uses simulated coords. To hook native hardware GPS location reports:
1. Under `AndroidManifest.xml`, request required hardware permissions:
   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   ```
2. Inside `MainActivity.kt`, initialize standard `FusedLocationProviderClient`:
   ```kotlin
   val locationClient = LocationServices.getFusedLocationProviderClient(this)
   locationClient.requestLocationUpdates(...)
   ```
3. Relay latitude/longitude reports to update `viewModel.currentLat` and `viewModel.currentLng`.

### Blueprint B: Interfacing the Morse Terminal with Baofeng VHF Radios
To transmit the encoded Vigenère ciphertext directly over VHF radio channels without mobile networks:
1. Use an analog cable linking the smartphone auxiliary headphone jack to the Baofeng Handy Microphone/PTT (Push-To-Talk) connector.
2. Inside `SurvivalViewModel.kt`, upgrade `triggerMorseFlash()` to play high-frequency audio tones (e.g., $800\text{ Hz}$ sine wave) matching standard Morse timings:
   ```kotlin
   val audioTrack = AudioTrack.Builder()
       .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build())
       .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(44100).build())
       .build()
   // Stream 800 Hz tones matching Dot (200ms) and Dash (600ms) over the AUX cable!
   ```

### Blueprint C: Mesh Link via Meshtastic LoRa Bluetooth Serial
To replace the offline simulated community bulletin board in `SolidarityScreen` with live LoRa packet transmissions:
1. Integrate the official **Meshtastic Android SDK** or setup a simple SPP Bluetooth Serial connection to standard ESP32-LoRa T-Beam modules.
2. Listen to incoming raw LoRa packets (usually transmitted on UHF 915 MHz/433 MHz), unpack the bytes, decode Vigenère payload structures, and insert them directly into the database:
   ```kotlin
   fun onReceiveBluetoothLoRaPacket(payloadBytes: ByteArray) {
       val packetString = String(payloadBytes)
       // Parse and add directly to viewModel.solidarityPosts!
   }
   ```

### Blueprint D: Coupling with Local Wi-Fi Communities (Internet-in-a-Box Integration)
Connect the application natively to standard local Wi-Fi micro-portals (IIAB) powered by Raspberry Pi hardware:
1. Query locally available Wi-Fi access points.
2. If an open Wi-Fi AP titled `"Internet-in-a-Box"` is detected, redirect the offline search tab view directly to the REST directory hosted on the local Raspberry web server (`http://10.10.10.1/`):
   ```kotlin
   val localWikiUrl = "http://10.10.10.1/kiwix/wikipedia_es_all/"
   // Integrate WebView inside GuidesScreen when IIAB connection is secured offline!
   ```

---
*This technical Master Blueprint ensures that any engineering team can take these architectural blueprints and construct a truly resilient global network of human solidarity and off-grid communication.*
