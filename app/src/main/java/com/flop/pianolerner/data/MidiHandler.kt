/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import android.media.midi.MidiDevice
import android.media.midi.MidiManager
import android.media.midi.MidiReceiver

class MidiHandler(val midiDevice: MidiDevice, val midiManager: MidiManager) : MidiReceiver() {
    fun startListen() {
        val port = this.midiDevice.openOutputPort(0) ?: return
        port.connect(this)
    }

    override fun onSend(p0: ByteArray?, p1: Int, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }
}