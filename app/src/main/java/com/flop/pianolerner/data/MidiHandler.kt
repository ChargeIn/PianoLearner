/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import android.media.midi.MidiDevice
import android.media.midi.MidiOutputPort
import android.media.midi.MidiReceiver

class MidiHandler() : MidiReceiver() {

    var connectedPort: MidiOutputPort? = null
    var callback: ((NoteOnEvent) -> Unit) = {}

    constructor(midiDevice: MidiDevice) : this() {
        val port = midiDevice.openOutputPort(0) ?: return
        connectedPort = port
        port.connect(this)
    }

    fun addNoteOnListener(callback: (NoteOnEvent) -> Unit) {
        this.callback = callback
    }

    // This assumes the message has been aligned using a MidiFramer
    // so that the first byte is a status byte (including the offset).
    override fun onSend(msg: ByteArray, offset: Int, cout: Int, t: Long) {
        val reader = MidiMessageReader(msg, offset)

        // https://midi.org/midi-over-bluetooth-low-energy-ble-midi
        try {
            val event = this.parseDataMsg(reader, t.toInt())
            val next = reader.peek1Byte()
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
                this.callback(event)
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

    private fun isBitSet(byte: Byte, pos: Int): Boolean {
        return (byte.toInt() shr pos) == 1
    }
}