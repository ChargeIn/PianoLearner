/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flop.pianolerner.data.BLDevicesViewModel

@Composable
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
fun PlayPiano(
    modifier: Modifier = Modifier,
    devicesViewModel: BLDevicesViewModel,
    navController: NavController
) {
    val context = LocalContext.current


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            return Text("Error: Your device does not support the midi service feature.")
        }

        Button(
            onClick = {
                devicesViewModel.startPlay(context)
            },
            modifier = modifier.padding(8.dp)
        ) {
            Text("Start")
        }
    }
}