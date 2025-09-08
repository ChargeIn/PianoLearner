/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import androidx.compose.runtime.mutableStateListOf
import kotlin.random.Random

class Game(private var handler: MidiHandler) {

    val notes = mutableStateListOf<Note>()
    var lastNote = -1;

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
        this.notes.clear()

        val nodesToPick = listOf(0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21)
        var randomIndex = Random.nextInt(nodesToPick.size);
        var randomElement = nodesToPick[randomIndex]

        while (randomElement == this.lastNote) {
            randomIndex = Random.nextInt(nodesToPick.size);
            randomElement = nodesToPick[randomIndex]
        }
        this.lastNote = randomElement

        // 14 notes for violin lines
        this.notes.add(Note(randomElement + 60, Clef.VIOLIN, false))
    }
}