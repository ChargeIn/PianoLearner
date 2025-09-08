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
    var showLowerLine = false
    var showUpperLine = false

    init {
        // one octave is an increment of 12 ( 7 white keys and 5 black)
        // Violin clef starts at 60
        val violinBase: Int = this.note - 60
        val octave: Int = violinBase / 12
        val rest: Int = violinBase - octave * 12

        this.showLowerLine = this.note < 62
        this.showUpperLine = this.note > 80;

        when (rest) {
            0, 1 -> {
                this.pos = octave * 7
            }

            2, 3 -> {
                this.pos = octave * 7 + 1
            }

            4 -> {
                this.pos = octave * 7 + 2
            }

            5, 6 -> {
                this.pos = octave * 7 + 3
            }

            7, 8 -> {
                this.pos = octave * 7 + 4
            }

            9, 10 -> {
                this.pos = octave * 7 + 5
            }

            else -> {
                this.pos = octave * 7 + 6
            }

        }
    }

    fun isHit(event: NoteOnEvent): Boolean {
        this.pressed = event.note == this.note
        return this.pressed
    }
}