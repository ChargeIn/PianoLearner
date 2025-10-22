package org.flop.plugin.android.piano_connector

import android.content.Context
import android.media.midi.MidiManager
import android.media.midi.MidiReceiver
import android.os.Build
import androidx.annotation.RequiresApi
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class PianoConnectorAndroidPlugin(godot: Godot) : GodotPlugin(godot) {

    val bluetoothHandler = BluetoothHandler(this, this.activity!!)

    @RequiresApi(Build.VERSION_CODES.M)
    val midiManager =
        this.activity!!.applicationContext!!.getSystemService(Context.MIDI_SERVICE) as MidiManager
    lateinit var midiHelper: MidiHelper

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    fun emitSignal(signalName: String, signalArgs: Any) {
        super.emitSignal(signalName, signalArgs)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @UsedByGodot
    fun scanBLEDevices() {
        this.bluetoothHandler.startScan()
    }

    @UsedByGodot
    fun stopScan() {
        this.bluetoothHandler.stopScan()
    }

    @UsedByGodot
    fun enableLocation() {
        this.bluetoothHandler.enableLocation()
    }

    @UsedByGodot
    fun getScanResults(): String {
        return this.bluetoothHandler.devices.joinToString("<,>") { scanResult ->
            val address = scanResult.device.address
            val name = scanResult.scanRecord?.deviceName ?: address
            return@joinToString name.plus(" ( IP: ").plus(address).plus(")")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMainRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>?,
        grantResults: IntArray?
    ) {
        super.onMainRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == this.bluetoothHandler.PERMISSION_REQUEST_CODE) {
            this.bluetoothHandler.onRequestPermissionsResult(permissions, grantResults)
        }
    }

    override fun getPluginSignals(): Set<SignalInfo> {
        return setOf(
            SignalInfo("notePressed", String::class.java),
            SignalInfo("bluetoothHandler", String::class.java),
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    inner class MidiHelper : MidiReceiver() {
        // This assumes the message has been aligned using a MidiFramer
        // so that the first byte is a status byte (including the offset).
        override fun onSend(msg: ByteArray, offset: Int, cout: Int, t: Long) {
            val reader = MidiMessageReader(msg, offset)

            // https://midi.org/midi-over-bluetooth-low-energy-ble-midi
            try {
                this.parseDataMsg(reader, t.toInt())
                reader.peek1Byte()
            } catch (e: Exception) {
                // TODO: Implement and save in app log
            }
        }

        /**
         * Thank you to
         * https://github.com/LucasAlfare/FLMidi/blob/9c9296af36276d91a6196c721e1f5332bce9e1b2/src/main/kotlin/com/lucasalfare/flmidi/MidiParser.kt
         * https://github.com/philburk/android-midisuite/blob/master/MidiScope/src/main/java/com/mobileer/example/midiscope/MidiPrinter.java
         * for some inspiration
         */
        private fun parseDataMsg(reader: MidiMessageReader, deltaTime: Int): MidiDataEvent {
            val status = reader.read1Byte()
            val channel = status and 0x0F
            val controlCode = (status and 0xFF) shr 4
            val controlType = MidiStatusCodes.fromCode(controlCode)

            return when (controlType) {
                MidiStatusCodes.NoteOn -> {
                    val note = reader.read1Byte()
                    val velocity = reader.read1Byte()
                    val event = NoteOnEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        note = note,
                        velocity = velocity
                    )
                    return event
                }

                MidiStatusCodes.NoteOff -> {
                    val note = reader.read1Byte()
                    val velocity = reader.read1Byte()
                    NoteOffEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        note = note,
                        velocity = velocity
                    )
                }

                MidiStatusCodes.PolyphonicKeyPressure -> {
                    val note = reader.read1Byte()
                    val pressure = reader.read1Byte()
                    PolyphonicKeyPressureEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        note = note,
                        pressure = pressure
                    )
                }

                MidiStatusCodes.ControlChange -> {
                    val controller = reader.read1Byte()
                    val value = reader.read1Byte()
                    ControlChangeEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        controller = controller,
                        value = value
                    )
                }

                MidiStatusCodes.ProgramChange -> {
                    val program = reader.read1Byte()
                    ProgramChangeEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        program = program
                    )
                }

                MidiStatusCodes.ChannelPressure -> {
                    val pressure = reader.read1Byte()
                    ChannelPressureEvent(
                        deltaTime = deltaTime,
                        channel = channel,
                        pressure = pressure
                    )
                }

                MidiStatusCodes.PitchBend -> {
                    val lsb = reader.read1Byte()
                    val msb = reader.read1Byte()
                    val bend = (msb shl 7) or lsb
                    PitchBendEvent(deltaTime = deltaTime, channel = channel, bend = bend)
                }
            }
        }
    }
}


