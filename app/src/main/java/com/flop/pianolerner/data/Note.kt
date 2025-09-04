/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

enum class Clef {
    VIOLIN,
    BASS
}


class Note(
    val note: Int, val clef: Clef, var pressed: Boolean
) {
    fun isHit(event: NoteOnEvent): Boolean {
        // LOGIC is quite basic and only for testing
        // violin normal "C" is number 60 on NoteOnEvent
        this.pressed =
            this.note == (event.note - 60) / 2 // TODO fix logic and draw position correctly
        return this.pressed
    }
}