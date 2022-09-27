package com.example.apptemplate.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.CreateNoteUseCase
import com.example.apptemplate.domain.EditNoteUseCase
import com.example.apptemplate.domain.GetNoteUseCase
import com.example.apptemplate.fakesource.FakeNotesRepository
import com.example.apptemplate.navigation.NotesArguments
import com.example.apptemplate.ui.details.NoteDetailsViewModel
import com.example.apptemplate.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class NoteDetailsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    private lateinit var noteDetailsViewModel: NoteDetailsViewModel

    private lateinit var notesRepository: NotesRepository

    @Before
    fun setUp() {
        notesRepository = FakeNotesRepository()
    }


    @Test
    fun `test initial state before creating new note`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel()

        val state = noteDetailsViewModel.uiStateFlow.value

        assertTrue(state.isNewNote)
        assertFalse(state.isNoteSaved)
        assertEquals(state.title, "")
        assertEquals(state.text, "")
        assertNull(state.errorMessage)

        collectJob.cancel()
    }

    @Test
    fun `test create new note success`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel()

        val title = "Title"
        val text = "Text"
        noteDetailsViewModel.onTitleChanged(title)
        noteDetailsViewModel.onTextChanged(text)
        kotlin.run {
            val state = noteDetailsViewModel.uiStateFlow.value
            assertEquals(state.title, title)
            assertEquals(state.text, text)
        }

        noteDetailsViewModel.saveNote()

        kotlin.run {
            val state = noteDetailsViewModel.uiStateFlow.value

            assertTrue(state.isNewNote)
            assertTrue(state.isNoteSaved)
            assertEquals(state.title, title)
            assertEquals(state.text, text)
            assertNull(state.errorMessage)
        }

        val savedNotes = notesRepository.notesFlow.first()
        assertEquals(1, savedNotes.size)
        assertTrue(savedNotes.any { it.title == title && it.text == text })

        collectJob.cancel()
    }

    @Test
    fun `test try save empty note`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel()

        val title = ""
        val text = ""
        noteDetailsViewModel.onTitleChanged(title)
        noteDetailsViewModel.onTextChanged(text)

        noteDetailsViewModel.saveNote()

        val state = noteDetailsViewModel.uiStateFlow.value

        assertFalse(state.isNoteSaved)
        assertNotNull(state.errorMessage)

        val savedNotes = notesRepository.notesFlow.first()
        assertEquals(0, savedNotes.size)

        collectJob.cancel()
    }

    @Test
    fun `test initial state before editing existing note`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel(notesAmount = 3, noteId = 1)

        val expectedNote = notesRepository.getNoteEntityById(1).getOrThrow()
        val state = noteDetailsViewModel.uiStateFlow.value
        assertEquals(expectedNote.title, state.title)
        assertEquals(expectedNote.text, state.text)
        assertFalse(state.isNewNote)
        assertFalse(state.isNoteSaved)
        assertNull(state.errorMessage)

        collectJob.cancel()
    }

    @Test
    fun `test edit existing note success`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel(notesAmount = 3, noteId = 1)

        val initialNote = notesRepository.getNoteEntityById(1).getOrThrow()

        val title = "New title"
        val text = "New text"
        noteDetailsViewModel.onTitleChanged(title)
        noteDetailsViewModel.onTextChanged(text)

        noteDetailsViewModel.saveNote()

        val state = noteDetailsViewModel.uiStateFlow.value
        assertFalse(state.isNewNote)
        assertTrue(state.isNoteSaved)
        assertEquals(state.title, title)
        assertEquals(state.text, text)
        assertNull(state.errorMessage)

        val updatedNote = notesRepository.getNoteEntityById(1).getOrThrow()

        assertEquals(initialNote.id, updatedNote.id)
        assertEquals(initialNote.timeCreatedMillis, updatedNote.timeCreatedMillis)
        assertEquals(title, updatedNote.title)
        assertEquals(text, updatedNote.text)

        collectJob.cancel()
    }

    @Test
    fun `test on error message shown`() = runTest {
        val collectJob = launch { noteDetailsViewModel.uiStateFlow.collect() }
        initViewModel()

        val title = ""
        val text = ""
        noteDetailsViewModel.onTitleChanged(title)
        noteDetailsViewModel.onTextChanged(text)

        noteDetailsViewModel.saveNote()

        kotlin.run {
            val state = noteDetailsViewModel.uiStateFlow.value
            assertNotNull(state.errorMessage)
        }

        noteDetailsViewModel.onErrorMessageShown()

        kotlin.run {
            val state = noteDetailsViewModel.uiStateFlow.value
            assertNull(state.errorMessage)
        }

        collectJob.cancel()
    }

    private fun initViewModel(
        noteId: Int? = null,
        notesAmount: Int = 0
    ) {
        populateRepo(notesAmount)
        noteDetailsViewModel = NoteDetailsViewModel(
            EditNoteUseCase(notesRepository),
            CreateNoteUseCase(notesRepository),
            GetNoteUseCase(notesRepository),
            SavedStateHandle(mapOf(NotesArguments.noteId to noteId))
        )
    }

    private fun populateRepo(notesAmount: Int = 3): List<NoteEntity> = runBlocking {
        repeat(notesAmount) {
            notesRepository.insertNoteEntity(
                NoteEntity(
                    id = it,
                    title = "Title$it",
                    text = "Text$it",
                    timeCreatedMillis = System.currentTimeMillis() + it * 100
                )
            )
        }
        notesRepository.notesFlow.first()
    }
}