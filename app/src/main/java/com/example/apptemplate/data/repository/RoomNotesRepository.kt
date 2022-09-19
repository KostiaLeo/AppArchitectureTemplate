package com.example.apptemplate.data.repository

import com.example.apptemplate.data.source.NotesDataSource
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.data.source.local.room.NotesDao
import com.example.apptemplate.di.IODispatcher
import com.example.apptemplate.di.LocalSource
import com.example.apptemplate.di.RemoteSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomNotesRepository @Inject constructor(
    private val notesDao: NotesDao
) : NotesRepository {
    override val notesFlow: Flow<List<NoteEntity>> = notesDao.observeNoteEntities()

    override suspend fun getNoteEntityById(id: Int): Result<NoteEntity> = kotlin.runCatching {
        val note = notesDao.getNoteEntityById(id)
        requireNotNull(note) { "Note with id $id is note found" }
    }

    override suspend fun insertNoteEntity(entity: NoteEntity) {
        notesDao.insertNoteEntity(entity)
    }

    override suspend fun deleteAllNoteEntities() {
        notesDao.deleteAllNoteEntities()
    }

    override suspend fun deleteNoteEntityById(id: Int) {
        notesDao.deleteNoteEntityById(id)
    }

    override suspend fun updateNoteEntity(entity: NoteEntity) {
        notesDao.updateNoteEntity(entity)
    }
}