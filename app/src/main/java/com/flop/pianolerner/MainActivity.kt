package com.flop.pianolerner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flop.pianolerner.ui.theme.PianoLernerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PianoLernerTheme {
                Scaffold { padding ->
                    val modifier = Modifier.padding(padding)
                    val navController = rememberNavController()

                    NavHost(
                        modifier = modifier,
                        navController = navController,
                        startDestination = "bluetooth_scan",
                        builder = {
                            composable("bluetooth_scan") {
                                BluetoothScan()
                            }
                        })
                }
            }
        }
    }
}


