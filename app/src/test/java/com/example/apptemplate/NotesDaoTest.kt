package com.example.apptemplate

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.data.source.local.room.NotesDao
import com.example.apptemplate.data.source.local.room.NotesDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_TITLE = "Test title"
private const val TEST_TEXT = "Test text text text text"

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NotesDaoTest {
    private lateinit var notesDatabase: NotesDatabase
    private lateinit var notesDao: NotesDao

    private val testEntity = NoteEntity(
        title = TEST_TITLE,
        text = TEST_TEXT
    )

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        notesDatabase = Room.inMemoryDatabaseBuilder(
            context, NotesDatabase::class.java
        ).allowMainThreadQueries().build()
        notesDao = notesDatabase.notesDao
    }

    @After
    fun closeDB() {
        notesDatabase.close()
    }

    @Test
    fun `test note is inserted`() = runTest {
        notesDao.insertNoteEntity(testEntity)

        val firstNote = notesDao.getNoteEntities().first()
        assertNoteContentsAreEqual(firstNote, testEntity)
    }

    @Test
    fun `test 2 notes are inserted and fetched ordered by last edited`() = runTest {
        val firstEntity = NoteEntity(
            "Title1",
            "text1"
        )
        notesDao.insertNoteEntity(firstEntity)
        val secondEntity = NoteEntity(
            "Title2",
            "text2"
        )
        notesDao.insertNoteEntity(secondEntity)

        val notes = notesDao.getNoteEntities()
        assertNoteContentsAreEqual(notes[0], secondEntity)
        assertNoteContentsAreEqual(notes[1], firstEntity)
    }

    @Test
    fun `test note is updated and pushed to the top`() = runTest {
        val notes = fillUpDB(notesAmount = 3)
        val editedNote = notes[2].copy(
            text = "Edited text",
            timeLastEditedMillis = System.currentTimeMillis()
        )

        notesDao.updateNoteEntity(editedNote)

        val updatedNotes = notesDao.getNoteEntities()

        assertNoteContentsAreEqual(
            updatedNotes.first(),
            editedNote
        )
    }

    @Test
    fun `test note is deleted`() = runTest {
        val notes = fillUpDB(notesAmount = 3)
        val deletedNote = notes[1]

        notesDao.deleteNoteEntityById(deletedNote.id)

        assertTrue(!notesDao.getNoteEntities().contains(deletedNote))
    }

    @Test
    fun `test all notes are deleted`() = runTest {
        fillUpDB(notesAmount = 3)

        notesDao.deleteAllNoteEntities()

        assertTrue(notesDao.getNoteEntities().isEmpty())
    }

    @Test
    fun `select not existing note`() = runTest {
        assertNull(notesDao.getNoteEntityById(0))
    }

    private suspend fun fillUpDB(notesAmount: Int = 2): List<NoteEntity> {
        repeat(notesAmount) {
            val entity = NoteEntity(
                "Title$it",
                "text$it"
            )
            notesDao.insertNoteEntity(entity)
        }

        return notesDao.getNoteEntities()
    }

    private fun assertNoteContentsAreEqual(actualNote: NoteEntity, expectedNote: NoteEntity) {
        assertEquals(actualNote.text, expectedNote.text)
        assertEquals(actualNote.title, expectedNote.title)
        assertEquals(actualNote.timeCreatedMillis, expectedNote.timeCreatedMillis)
        assertEquals(actualNote.timeLastEditedMillis, expectedNote.timeLastEditedMillis)
    }
}