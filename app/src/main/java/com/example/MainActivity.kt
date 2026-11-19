package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.core.security.configureAppCheck
import com.example.ui.FinSmartApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase App Check synchronously before creating ViewModels or rendering AI/Firestore UI.
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            FinSmartApp()
        }
    }
}
