package com.example.apptemplate.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.apptemplate.HiltTestActivity
import com.example.apptemplate.R
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.CreateNoteUseCase
import com.example.apptemplate.domain.EditNoteUseCase
import com.example.apptemplate.domain.GetNoteUseCase
import com.example.apptemplate.navigation.NotesArguments
import com.example.apptemplate.ui.details.NoteDetailsScreen
import com.example.apptemplate.ui.details.NoteDetailsViewModel
import com.example.apptemplate.utils.findTextField
import com.example.apptemplate.utils.populateRepository
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class NoteDetailsScreenTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var notesRepository: NotesRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun setContent(noteId: Int?) {
        val args = mapOf(NotesArguments.noteId to noteId)
        val viewModel = NoteDetailsViewModel(
            EditNoteUseCase(notesRepository),
            CreateNoteUseCase(notesRepository),
            GetNoteUseCase(notesRepository),
            SavedStateHandle(args)
        )
        composeTestRule.setContent {
            AppCompatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteDetailsScreen(
                        viewModel = viewModel,
                        onNavigateUp = {}
                    )
                }
            }
        }
    }

    @Test
    fun testCreateNewNote() {
        setContent(noteId = null)

        val title = "Test title"
        val text = "Test text"
        with(composeTestRule) {
            findTextField(R.string.type_title_hint).performTextInput(title)
            findTextField(R.string.type_text_hint).performTextInput(text)
        }
        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note))
            .assertIsDisplayed()
            .performClick()

        val savedNote = runBlocking { notesRepository.notesFlow.first().first() }

        assertEquals(title, savedNote.title)
        assertEquals(text, savedNote.text)
    }

    @Test
    fun testCreateNewNoteWithExisting() {
        notesRepository.populateRepository(notesAmount = 3)

        setContent(noteId = null)

        val title = "Test title"
        val text = "Test text"
        with(composeTestRule) {
            findTextField(R.string.type_title_hint).performTextInput(title)
            findTextField(R.string.type_text_hint).performTextInput(text)
        }
        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note))
            .assertIsDisplayed()
            .performClick()

        val notes = runBlocking { notesRepository.notesFlow.first() }
        val savedNote = notes.first()

        assertEquals(title, savedNote.title)
        assertEquals(text, savedNote.text)
    }

    @Test
    fun testEditExistingNote() {
        val notes: List<NoteEntity> = notesRepository.populateRepository(notesAmount = 3)
        val noteToEdit = notes.last()
        setContent(noteId = noteToEdit.id)

        val title = "Test title"
        val text = "Test text"
        with(composeTestRule) {
            onNodeWithText(noteToEdit.title).performTextClearance()
            findTextField(R.string.type_title_hint).performTextInput(title)
            onNodeWithText(noteToEdit.text).performTextClearance()
            findTextField(R.string.type_text_hint).performTextInput(text)
        }

        Espresso.closeSoftKeyboard()

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note)).performClick()

        val savedNote = runBlocking { notesRepository.notesFlow.first().first() }

        assertEquals(noteToEdit.id, savedNote.id)
        assertEquals(title, savedNote.title)
        assertEquals(text, savedNote.text)
    }

    @Test
    fun testTrySaveEmptyNote() {
        setContent(noteId = null)

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note)).performClick()

        composeTestRule.onNodeWithText(activity.getString(R.string.empty_note_error)).assertExists().assertIsDisplayed()

        val notes = runBlocking { notesRepository.notesFlow.first() }
        assertEquals(0, notes.size)
    }
}