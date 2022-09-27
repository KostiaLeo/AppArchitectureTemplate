package com.example.apptemplate.domain

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.fakesource.FakeNotesPreferencesRepository
import com.example.apptemplate.fakesource.FakeNotesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DeleteNoteUseCaseTest {
    lateinit var deleteNoteUseCase: DeleteNoteUseCase

    lateinit var preferencesRepository: NotesPreferencesRepository
    lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
        preferencesRepository = FakeNotesPreferencesRepository()
        deleteNoteUseCase = DeleteNoteUseCase(repository, UnpinNoteUseCase(preferencesRepository))
    }

    @Test
    fun `test delete notes`() = runTest {
        val notes = (0..1).map { NoteEntity(id = it, title = "Title$it", text = "Text$it") }
            .onEach { repository.insertNoteEntity(it) }

        preferencesRepository.pinNote(1)
        assertEquals(repository.notesFlow.first().size, 2)
        assertEquals(preferencesRepository.pinnedNotesIdsFlow.first().size, 1)

        deleteNoteUseCase(1)

        assertEquals(repository.notesFlow.first(), listOf(notes[0]))
        assertEquals(preferencesRepository.pinnedNotesIdsFlow.first(), emptySet())
    }
}