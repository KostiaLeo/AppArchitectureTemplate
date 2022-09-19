package com.example.apptemplate.domain

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteAllNotesUseCaseTest {
    lateinit var deleteAllNoteUseCase: DeleteAllNotesUseCase

    lateinit var repository: NotesRepository
    lateinit var preferencesRepository: NotesPreferencesRepository

    @Before
    fun setUp() {
        repository = mockk()
        preferencesRepository = mockk()
        deleteAllNoteUseCase = DeleteAllNotesUseCase(repository, preferencesRepository)
    }

    @Test
    fun `test delete notes`() = runTest {
        coEvery { repository.deleteAllNoteEntities() } answers {}
        coEvery { preferencesRepository.clearAllPins() } answers {}

        deleteAllNoteUseCase()

        coVerifyAll {
            repository.deleteAllNoteEntities()
            preferencesRepository.clearAllPins()
        }
    }
}