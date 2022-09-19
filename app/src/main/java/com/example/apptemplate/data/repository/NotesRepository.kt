package com.example.apptemplate.data.repository

import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    val notesFlow: Flow<List<NoteEntity>>

    suspend fun getNoteEntityById(id: Int): Result<NoteEntity>

    suspend fun insertNoteEntity(entity: NoteEntity)

    suspend fun deleteAllNoteEntities()

    suspend fun deleteNoteEntityById(id: Int)

    suspend fun updateNoteEntity(entity: NoteEntity)
}