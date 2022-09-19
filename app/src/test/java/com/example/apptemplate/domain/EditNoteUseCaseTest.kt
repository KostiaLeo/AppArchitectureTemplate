package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
        repository = mockk()
        editNoteUseCase = EditNoteUseCase(repository)
    }

    @Test
    fun `test note is updated`() = runTest {
        val currentEntity = NoteEntity(
            title = "Old title",
            text = "Old text"
        )
        val slot = slot<NoteEntity>()

        coEvery { repository.updateNoteEntity(capture(slot)) } answers {}

        val title = "test title"
        val text = "text text"
        editNoteUseCase(currentEntity, title, text)

        coVerify {
            repository.updateNoteEntity(withArg {
                assertEquals(it.id, currentEntity.id)
                assertEquals(it.title, title)
                assertEquals(it.text, text)
                assertEquals(it.timeCreatedMillis, currentEntity.timeCreatedMillis)
                assertTrue(it.timeLastEditedMillis > currentEntity.timeLastEditedMillis)
            })
        }
    }
}