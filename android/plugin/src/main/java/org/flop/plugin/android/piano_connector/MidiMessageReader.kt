/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package org.flop.plugin.android.piano_connector

class MidiMessageReader(private val msg: ByteArray, private var index: Int) {
    fun read1Byte(): Int {
        return this.msg[this.index++].toInt()
    }

    fun peek1Byte(): Int {
        return this.msg[this.index].toInt()
    }
}