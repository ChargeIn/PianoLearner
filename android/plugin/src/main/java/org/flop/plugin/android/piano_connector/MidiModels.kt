/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */
package org.flop.plugin.android.piano_connector

enum class MidiStatusCodes(val code: Int) {
    NoteOff(0b1000),
    NoteOn(0b1001),
    PolyphonicKeyPressure(0b1010),
    ControlChange(0b1011),
    ProgramChange(0b1100),
    ChannelPressure(0b1101),
    PitchBend(0b1110);

    companion object {
        fun fromCode(code: Int): MidiStatusCodes =
            MidiStatusCodes.values().find { it.code == code } ?: throw IllegalArgumentException(
                "Unknown status code: ${code.toString(16)}"
            )
    }

    override fun toString() = "${this.name}(0b${code.toString(2).padStart(4, '0')})"
}


sealed class MidiEvent {
    abstract val deltaTime: Int
}

sealed class MidiDataEvent : MidiEvent() {
    abstract val eventType: MidiStatusCodes
    abstract val channel: Int
    val statusByte: Int get() = (eventType.code shl 4) or (channel and 0x0F)
}

data class NoteOnEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.NoteOn,
    override val deltaTime: Int,
    override val channel: Int,
    val note: Int,
    val velocity: Int
) : MidiDataEvent() {
    override fun toString(): String {
        return "NoteOn," + this.note
    }
}

data class NoteOffEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.NoteOff,
    override val deltaTime: Int,
    override val channel: Int,
    val note: Int,
    val velocity: Int
) : MidiDataEvent() {
    override fun toString(): String {
        return "NoteOff," + this.note
    }
}

data class PolyphonicKeyPressureEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.PolyphonicKeyPressure,
    override val deltaTime: Int,
    override val channel: Int,
    val note: Int,
    val pressure: Int
) : MidiDataEvent()

data class ControlChangeEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.ControlChange,
    override val deltaTime: Int,
    override val channel: Int,
    val controller: Int,
    val value: Int
) : MidiDataEvent()

data class ProgramChangeEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.ProgramChange,
    override val deltaTime: Int,
    override val channel: Int,
    val program: Int
) : MidiDataEvent()

data class ChannelPressureEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.ChannelPressure,
    override val deltaTime: Int,
    override val channel: Int,
    val pressure: Int
) : MidiDataEvent()

data class PitchBendEvent(
    override val eventType: MidiStatusCodes = MidiStatusCodes.PitchBend,
    override val deltaTime: Int,
    override val channel: Int,
    val bend: Int
) : MidiDataEvent()