package com.example.apptemplate.utils

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun NotesRepository.insertNoteBlocking(noteEntity: NoteEntity) {
    runBlocking { insertNoteEntity(noteEntity) }
}

fun NotesRepository.populateRepository(notesAmount: Int = 3): List<NoteEntity> {
    return runBlocking {
        repeat(notesAmount) {
            insertNoteEntity(
                NoteEntity(
                    id = it,
                    title = "Title$it",
                    text = "Text$it",
                    timeCreatedMillis = System.currentTimeMillis() - (notesAmount - it) * 1000
                )
            )
        }
        notesFlow.first()
    }
}

fun NotesPreferencesRepository.pinNoteBlocking(noteId: Int) {
    runBlocking { pinNote(noteId) }
}