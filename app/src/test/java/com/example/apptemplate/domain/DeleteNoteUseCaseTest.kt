package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteNoteUseCaseTest {
    lateinit var deleteNoteUseCase: DeleteNoteUseCase

    lateinit var repository: NotesRepository
    lateinit var unpinNoteUseCase: UnpinNoteUseCase

    @Before
    fun setUp() {
        repository = mockk()
        unpinNoteUseCase = mockk()
        deleteNoteUseCase = DeleteNoteUseCase(repository, unpinNoteUseCase)
    }

    @Test
    fun `test delete notes`() = runTest {
        coEvery { repository.deleteNoteEntityById(any()) } answers {}
        coEvery { unpinNoteUseCase.invoke(any()) } answers {}

        val noteId = 1
        deleteNoteUseCase(noteId)

        coVerifyAll {
            repository.deleteNoteEntityById(noteId)
            unpinNoteUseCase.invoke(noteId)
        }
    }
}