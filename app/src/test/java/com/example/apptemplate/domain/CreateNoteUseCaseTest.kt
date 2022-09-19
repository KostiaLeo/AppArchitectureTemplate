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

class CreateNoteUseCaseTest {

    lateinit var createNoteUseCase: CreateNoteUseCase

    lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        repository = mockk()
        createNoteUseCase = CreateNoteUseCase(repository)
    }

    @Test
    fun `test create note`() = runTest {
        val slot = slot<NoteEntity>()

        coEvery { repository.insertNoteEntity(capture(slot)) } answers {}

        val title = "test title"
        val text = "text text"
        createNoteUseCase(title, text)

        coVerify {
            repository.insertNoteEntity(withArg {
                assertEquals(it.title, title)
                assertEquals(it.text, text)
            })
        }
    }
}