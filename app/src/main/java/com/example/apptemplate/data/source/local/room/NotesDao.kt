package com.example.apptemplate.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY last_edited DESC")
    fun observeNoteEntities(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY last_edited DESC")
    suspend fun getNoteEntities(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteEntityById(id: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNoteEntity(entity: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNoteEntities()

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteEntityById(id: Int)

    @Update
    suspend fun updateNoteEntity(entity: NoteEntity)
}