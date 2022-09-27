package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.fakesource.FakeNotesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditNoteUseCaseTest {
    lateinit var editNoteUseCase: EditNoteUseCase

    lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
        editNoteUseCase = EditNoteUseCase(repository)
    }

    @Test
    fun `test note is updated`() = runTest {
        val currentEntity = NoteEntity(
            title = "Old title",
            text = "Old text"
        )

        repository.insertNoteEntity(currentEntity)

        val title = "test title"
        val text = "text text"
        editNoteUseCase(currentEntity, title, text)

        val updatedNote = repository.getNoteEntityById(currentEntity.id).getOrThrow()

        assertEquals(title, updatedNote.title)
        assertEquals(text, updatedNote.text)
        assertEquals(currentEntity.timeCreatedMillis, updatedNote.timeCreatedMillis)
        assertTrue(updatedNote.timeLastEditedMillis > currentEntity.timeLastEditedMillis)
    }
}