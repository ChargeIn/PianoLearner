/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */
import android.content.pm.PackageManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.flop.pianolerner.data.BLDevicesViewModel
import com.flop.pianolerner.data.Clef
import com.flop.pianolerner.data.Game
import com.flop.pianolerner.data.MidiHandler
import com.flop.pianolerner.data.Note

@Composable
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
fun PlayPiano(
    modifier: Modifier = Modifier,
    devicesViewModel: BLDevicesViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    if (devicesViewModel.midiDevice == null) {
        navController.navigate("bluetooth_scan")
        return
    }

    val game = remember {
        Game(MidiHandler(devicesViewModel.midiDevice!!))
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            return Text("Error: Your device does not support the midi service feature.")
        }

        Canvas(
            modifier = modifier
                .fillMaxSize()
                .background(Color(63, 156, 160))
        ) {
            drawNotes(game.notes)
        }
    }
}

const val NOTE_WIDTH = 56f
const val NOTE_HEIGHT = 40f
const val HALF_NOTE_HEIGHT = NOTE_HEIGHT / 2
const val LINE_STROKE = 8f
const val LINE_HEIGHT = (NOTE_HEIGHT + LINE_STROKE).toInt()
const val HALF_LINE_HEIGHT = LINE_HEIGHT / 2
const val LINE_OFFSET = 100
const val ROW_HEIGHT = LINE_HEIGHT * 4f

private fun DrawScope.drawNotes(
    notes: List<Note>,
) {
    val violinLinesStart = center.y - LINE_OFFSET

    // violin background
    drawRect(Color.White, Offset(0f, violinLinesStart - ROW_HEIGHT), Size(size.width, ROW_HEIGHT))

    for (i in 0..200 step LINE_HEIGHT) {
        val y = violinLinesStart - i
        drawLine(Color.Black, Offset(0f, y), Offset(size.width, y), 8f)
    }

    val baseLinesStart = center.y + LINE_OFFSET

    // base background
    drawRect(Color.White, Offset(0f, baseLinesStart), Size(size.width, ROW_HEIGHT))

    for (i in 0..200 step LINE_HEIGHT) {
        val y = baseLinesStart + i
        drawLine(Color.Black, Offset(0f, y), Offset(size.width, y), LINE_STROKE)
    }

    val lastViolinLine = center.y - LINE_OFFSET + LINE_HEIGHT - HALF_NOTE_HEIGHT

    notes.forEach { note ->
        if (note.clef == Clef.VIOLIN) {
            drawOval(
                Color.Black,
                Offset(
                    size.width / 2,
                    lastViolinLine - note.note * HALF_LINE_HEIGHT
                ),
                Size(NOTE_WIDTH, NOTE_HEIGHT)
            )
        }
    }
}