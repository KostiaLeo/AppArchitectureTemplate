package com.example.apptemplate.viewmodel

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.DeleteAllNotesUseCase
import com.example.apptemplate.domain.DeleteNoteUseCase
import com.example.apptemplate.domain.ObserveNotesUseCase
import com.example.apptemplate.domain.ObservePinnedNotesUseCase
import com.example.apptemplate.domain.PinNoteUseCase
import com.example.apptemplate.domain.UnpinNoteUseCase
import com.example.apptemplate.fakesource.FakeNotesPreferencesRepository
import com.example.apptemplate.fakesource.FakeNotesRepository
import com.example.apptemplate.ui.allNotes.AllNotesViewModel
import com.example.apptemplate.ui.allNotes.PinnableNote
import com.example.apptemplate.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class AllNotesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    private lateinit var allNotesViewModel: AllNotesViewModel

    private lateinit var notesRepository: NotesRepository

    private lateinit var notesPreferencesRepository: NotesPreferencesRepository

    @Before
    fun setUp() {
        notesRepository = FakeNotesRepository()
        notesPreferencesRepository = FakeNotesPreferencesRepository()

        val unpinNoteUseCase = UnpinNoteUseCase(notesPreferencesRepository)
        allNotesViewModel = AllNotesViewModel(
            ObserveNotesUseCase(notesRepository),
            ObservePinnedNotesUseCase(notesPreferencesRepository),
            PinNoteUseCase(notesPreferencesRepository),
            unpinNoteUseCase,
            DeleteAllNotesUseCase(notesRepository, notesPreferencesRepository),
            DeleteNoteUseCase(notesRepository, unpinNoteUseCase),
            testDispatcher
        )
    }

    @Test
    fun `test saved notes are in state`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }

        populateRepo()

        val state = allNotesViewModel.uiStateFlow.value

        val expectedIds = (0..2).reversed()
        assertContentEquals(expectedIds, state.notPinnedNotes.map { it.note.id })
        assertContentEquals(emptyList(), state.pinnedNotes)

        collectJob.cancel()
    }

    @Test
    fun `test note is pinned`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }

        val notes = populateRepo()
        allNotesViewModel.pinNote(notes[0])

        val state = allNotesViewModel.uiStateFlow.value
        val expectedPinnedNotesIds = listOf(notes[0].id)
        val expectedNotPinnedNotesIds = listOf(notes[1].id, notes[2].id)
        assertContentEquals(expectedPinnedNotesIds, state.pinnedNotes.map { it.note.id })
        assertContentEquals(expectedNotPinnedNotesIds, state.notPinnedNotes.map { it.note.id })

        val pinned = notesPreferencesRepository.pinnedNotesIdsFlow.first()
        assertContentEquals(setOf(notes[0].id) as Iterable<Int>, pinned as Iterable<Int>)

        collectJob.cancel()
    }

    @Test
    fun `test note is unpinned`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }

        val notes = populateRepo(notesAmount = 5)
        allNotesViewModel.pinNote(notes[1])
        allNotesViewModel.pinNote(notes[3])

        allNotesViewModel.unpinNote(notes[1])

        val state = allNotesViewModel.uiStateFlow.value
        val expectedPinnedNotesIds = listOf(notes[3].id)
        val expectedNotPinnedNotesIds = notes.filterIndexed { index, _ -> index != 3 }.map { it.id }
        assertContentEquals(expectedPinnedNotesIds, state.pinnedNotes.map { it.note.id })
        assertContentEquals(expectedNotPinnedNotesIds, state.notPinnedNotes.map { it.note.id })

        val pinned = notesPreferencesRepository.pinnedNotesIdsFlow.first()
        assertContentEquals(setOf(notes[3].id) as Iterable<Int>, pinned as Iterable<Int>)

        collectJob.cancel()
    }

    @Test
    fun `test note is deleted`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }

        val notes = populateRepo()

        allNotesViewModel.deleteNote(notes[0])

        val state = allNotesViewModel.uiStateFlow.value
        val expectedNotesIds = listOf(notes[1], notes[2]).map { it.id }
        assertContentEquals(expectedNotesIds, state.notPinnedNotes.map { it.note.id })
        assertContentEquals(expectedNotesIds, notesRepository.notesFlow.first().map { it.id })

        collectJob.cancel()
    }

    @Test
    fun `test pinned note is deleted`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }

        val notes = populateRepo()

        allNotesViewModel.pinNote(notes[0])
        allNotesViewModel.deleteNote(notes[0])

        val state = allNotesViewModel.uiStateFlow.value
        val expectedNotesIds = listOf(notes[1], notes[2]).map { it.id }
        assertContentEquals(expectedNotesIds, state.notPinnedNotes.map { it.note.id })
        assertContentEquals(emptyList(), state.pinnedNotes)

        assertContentEquals(expectedNotesIds, notesRepository.notesFlow.first().map { it.id })
        assertContentEquals(
            emptySet<Int>() as Iterable<Int>,
            notesPreferencesRepository.pinnedNotesIdsFlow.first() as Iterable<Int>
        )

        collectJob.cancel()
    }

    @Test
    fun `test all notes are deleted`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }
        populateRepo()

        allNotesViewModel.deleteAllNotes()

        val state = allNotesViewModel.uiStateFlow.value
        assertContentEquals(emptyList(), state.notPinnedNotes)
        assertContentEquals(emptyList(), state.pinnedNotes)
        assertContentEquals(emptyList(), notesRepository.notesFlow.first())
        assertContentEquals(emptyList(), notesPreferencesRepository.pinnedNotesIdsFlow.first())

        collectJob.cancel()
    }

    @Test
    fun `test note is focused`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }
        val notes = populateRepo()

        val focusedNote = PinnableNote(notes[0], false)
        allNotesViewModel.onNoteFocused(focusedNote)

        val state = allNotesViewModel.uiStateFlow.value
        assertEquals(focusedNote, state.focusedNote)

        collectJob.cancel()
    }

    @Test
    fun `test note is unfocused`() = runTest {
        val collectJob = launch(testDispatcher) { allNotesViewModel.uiStateFlow.collect() }
        val notes = populateRepo()

        val focusedNote = PinnableNote(notes[0], false)
        allNotesViewModel.onNoteFocused(focusedNote)
        allNotesViewModel.onNoteFocused(null)

        val state = allNotesViewModel.uiStateFlow.value
        assertNull(state.focusedNote)

        collectJob.cancel()
    }

    private suspend fun populateRepo(notesAmount: Int = 3): List<NoteEntity> {
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
        return notesRepository.notesFlow.first()
    }
}