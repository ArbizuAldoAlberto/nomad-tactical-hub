package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.SurvivalDatabase
import com.example.data.SurvivalRepository
import com.example.ui.SurvivalHubScreen
import com.example.ui.SurvivalViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.HardwareSensorBridge

class MainActivity : ComponentActivity() {
    private lateinit var database: SurvivalDatabase
    private lateinit var repository: SurvivalRepository
    private lateinit var sensorBridge: HardwareSensorBridge
    private lateinit var viewModel: SurvivalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SQLite Offline Room Local Database in WAL Mode with Safe Migration
        database = Room.databaseBuilder(
            applicationContext,
            SurvivalDatabase::class.java,
            "survival_hub_database"
        )
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addMigrations(SurvivalDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

        repository = SurvivalRepository(database.dao())
        sensorBridge = HardwareSensorBridge(applicationContext)

        // Setup ViewModel Custom Factory to bind repository and hardware sensor bridge
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SurvivalViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SurvivalViewModel(repository, sensorBridge) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, factory)[SurvivalViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.ui.theme.SlateCharcoal
                ) {
                    SurvivalHubScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sensorBridge.isInitialized) {
            sensorBridge.startSensors()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorBridge.isInitialized) {
            sensorBridge.stopSensors()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::sensorBridge.isInitialized) {
            sensorBridge.shutdown()
        }
    }
}
