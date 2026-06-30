package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.db.LegalHelperDatabase
import com.example.data.repository.LegalHelperRepository
import com.example.ui.screens.LegalHelperApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LegalHelperViewModel
import com.example.ui.viewmodel.LegalHelperViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database & DAOs offline
        val database = LegalHelperDatabase.getDatabase(applicationContext)
        val repository = LegalHelperRepository(
            legalQueryDao = database.legalQueryDao(),
            documentAnalysisDao = database.documentAnalysisDao(),
            lawyerBookingDao = database.lawyerBookingDao()
        )

        // Initialize ViewModel
        val factory = LegalHelperViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[LegalHelperViewModel::class.java]

        setContent {
            MyApplicationTheme {
                LegalHelperApp(viewModel = viewModel)
            }
        }
    }
}

