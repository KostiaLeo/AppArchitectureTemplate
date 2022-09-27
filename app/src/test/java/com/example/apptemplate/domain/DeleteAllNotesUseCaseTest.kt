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

class DeleteAllNotesUseCaseTest {
    lateinit var deleteAllNoteUseCase: DeleteAllNotesUseCase

    lateinit var preferencesRepository: NotesPreferencesRepository
    lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
        preferencesRepository = FakeNotesPreferencesRepository()
        deleteAllNoteUseCase = DeleteAllNotesUseCase(repository, preferencesRepository)
    }

    @Test
    fun `test delete notes`() = runTest {
        repository.insertNoteEntity(NoteEntity(id = 0, title = "Title", text = "Text"))
        repository.insertNoteEntity(NoteEntity(id = 1, title = "Title1", text = "Text1"))
        preferencesRepository.pinNote(0)
        assertEquals(repository.notesFlow.first().size, 2)
        assertEquals(preferencesRepository.pinnedNotesIdsFlow.first().size, 1)

        deleteAllNoteUseCase()

        assertEquals(repository.notesFlow.first(), emptyList())
        assertEquals(preferencesRepository.pinnedNotesIdsFlow.first(), emptySet())
    }
}