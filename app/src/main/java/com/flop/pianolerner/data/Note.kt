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
    val pos: Int
    val lineThrough: Boolean

    init {
        // one octave is an increment of 12 ( 7 white keys and 5 black)
        // Violin clef starts at 60
        val violinBase: Int = this.note - 60
        val octave: Int = violinBase / 12
        val rest: Int = violinBase - octave * 12

        if (rest > 4) {
            this.pos = octave * 7 + (rest + 1) / 2
        } else {
            this.pos = octave * 7 + rest / 2
        }
        this.lineThrough = this.pos % 2 == 0
    }

    fun isHit(event: NoteOnEvent): Boolean {
        this.pressed = event.note == this.note
        return this.pressed
    }
}