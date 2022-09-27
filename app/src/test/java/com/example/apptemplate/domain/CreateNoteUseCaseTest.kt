package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.fakesource.FakeNotesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CreateNoteUseCaseTest {

    lateinit var createNoteUseCase: CreateNoteUseCase

    lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
        createNoteUseCase = CreateNoteUseCase(repository)
    }

    @Test
    fun `test create note`() = runTest {
        val title = "test title"
        val text = "text text"
        createNoteUseCase(title, text)

        val note = repository.notesFlow.first().first()
        assertEquals(note.title, title)
        assertEquals(note.text, text)
    }
}