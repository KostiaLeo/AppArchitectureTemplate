package com.example.apptemplate.utils

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.runBlocking

fun NotesRepository.insertNoteBlocking(noteEntity: NoteEntity) {
    runBlocking { insertNoteEntity(noteEntity) }
}

fun NotesPreferencesRepository.pinNoteBlocking(noteId: Int) {
    runBlocking { pinNote(noteId) }
}