package com.example.apptemplate

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.repository.RoomNotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.data.source.local.room.NotesDatabase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomNotesRepositoryTest {
    private lateinit var notesDatabase: NotesDatabase

    private lateinit var repository: NotesRepository

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        notesDatabase = Room.inMemoryDatabaseBuilder(
            context, NotesDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RoomNotesRepository(notesDatabase.notesDao)
    }

    @After
    fun closeDB() {
        notesDatabase.close()
    }

    @Test
    fun `test get note with existing id`() = runTest {
        val notes = fillUpDB(3)

        val id = notes[0].id

        val result = repository.getNoteEntityById(id)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `test get note with not existing id`() = runTest {
        val id = 0
        val result = repository.getNoteEntityById(id)
        assertTrue(result.isFailure)
    }

    private suspend fun fillUpDB(notesAmount: Int = 2): List<NoteEntity> {
        repeat(notesAmount) {
            val entity = NoteEntity(
                "Title$it",
                "text$it"
            )
            repository.insertNoteEntity(entity)
        }

        return repository.notesFlow.first()
    }
}