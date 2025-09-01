/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

class MidiMessageReader(private val msg: ByteArray, private var index: Int) {
    fun read1Byte(): Int {
        return this.msg[this.index++].toInt()
    }

    fun peek1Byte(): Int {
        return this.msg[this.index].toInt()
    }
}