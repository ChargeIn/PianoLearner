/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import androidx.compose.runtime.mutableStateListOf

class Game(private var handler: MidiHandler) {

    val notes = mutableStateListOf<Note>()

    init {
        this.createNotes()

        this.handler.addNoteOnListener { event ->
            this.notes.forEach { note ->
                note.isHit(event)

                if (note.pressed) {
                    this.createNotes()
                }
            }
        }
    }

    fun createNotes() {
        this.notes.clear();

        // 14 notes for violin lines
        this.notes.add(Note((Math.random() * 12).toInt(), Clef.VIOLIN, false))
    }
}