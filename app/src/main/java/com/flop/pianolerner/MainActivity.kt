package com.flop.pianolerner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flop.pianolerner.data.BLDevicesViewModel
import com.flop.pianolerner.ui.theme.PianoLernerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PianoLernerTheme {
                val devicesViewModel = viewModel<BLDevicesViewModel>();

                Scaffold { padding ->
                    val modifier = Modifier.padding(padding)
                    val navController = rememberNavController()

                    NavHost(
                        modifier = modifier,
                        navController = navController,
                        startDestination = "bluetooth_scan",
                        builder = {
                            composable("bluetooth_scan") {
                                BluetoothScan(devicesViewModel = devicesViewModel)
                            }
                        })
                }
            }
        }
    }
}