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
import com.example.data.SurvivalDatabase
import com.example.data.SurvivalRepository
import com.example.ui.SurvivalHubScreen
import com.example.ui.SurvivalViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: SurvivalDatabase
    private lateinit var repository: SurvivalRepository
    private lateinit var viewModel: SurvivalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SQLite Offline Room Local Database
        database = Room.databaseBuilder(
            applicationContext,
            SurvivalDatabase::class.java,
            "survival_hub_database"
        ).fallbackToDestructiveMigration().build()

        repository = SurvivalRepository(database.dao())

        // Setup ViewModel Custom Factory to bind repository
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SurvivalViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SurvivalViewModel(repository) as T
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
}
