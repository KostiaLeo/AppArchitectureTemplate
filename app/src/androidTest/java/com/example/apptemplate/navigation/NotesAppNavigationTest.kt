package com.example.apptemplate.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.apptemplate.HiltTestActivity
import com.example.apptemplate.R
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.utils.findTextField
import com.example.apptemplate.utils.populateRepository
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class NotesAppNavigationTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var notesRepository: NotesRepository

    @Inject
    lateinit var notesPreferencesRepository: NotesPreferencesRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            AppCompatTheme {
                NotesAppNavHost()
            }
        }
    }

    @Test
    fun testCreateNewNote() {
        setContent()

        // click to create new note
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note)).performClick()

        // assert we're on the create note screen
        composeTestRule.onNodeWithText(activity.getString(R.string.create_note)).assertIsDisplayed()
        val inputTitle = "First Title"
        val inputText = "First Text"
        composeTestRule.findTextField(R.string.type_title_hint)
            .assertIsDisplayed()
            .performTextInput(inputTitle)
        composeTestRule.findTextField(R.string.type_text_hint)
            .assertIsDisplayed()
            .performTextInput(inputText)

        // save note
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note)).performClick()

        // assert we're back on all notes screen with saved note
        composeTestRule.onNodeWithText(activity.getString(R.string.notes)).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputText).assertIsDisplayed()
    }

    @Test
    fun testEditNote() {
        val notes: List<NoteEntity> = notesRepository.populateRepository(notesAmount = 3)
        setContent()

        val noteToEdit = notes.last()
        // click on existing note
        composeTestRule.onNodeWithText(notes.last().title).performClick()

        // assert we're on the edit note screen
        composeTestRule.onNodeWithText(activity.getString(R.string.edit_note)).assertIsDisplayed()
        val newTitle = "Test title"
        val newText = "Test text"
        with(composeTestRule) {
            onNodeWithText(noteToEdit.title).performTextClearance()
            findTextField(R.string.type_title_hint).performTextInput(newTitle)
            onNodeWithText(noteToEdit.text).performTextClearance()
            findTextField(R.string.type_text_hint).performTextInput(newText)
        }

        Espresso.closeSoftKeyboard()

        // save note
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note)).performClick()

        // assert we're back on all notes screen with saved note
        composeTestRule.onNodeWithText(activity.getString(R.string.notes)).assertIsDisplayed()

        composeTestRule.onNodeWithText(noteToEdit.title).assertDoesNotExist() // old title should be removed
        composeTestRule.onNodeWithText(noteToEdit.text).assertDoesNotExist() // old text should be removed

        composeTestRule.onNodeWithText(newTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(newText).assertIsDisplayed()
    }


    @Test
    fun testOpenCreateNoteScreen_andReturnBack() {
        setContent()

        // click to create new note
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note)).performClick()

        // assert we're on the create note screen
        composeTestRule.onNodeWithText(activity.getString(R.string.create_note)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.notes)).assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.back)).performClick()

        // assert we're back on all notes screen
        composeTestRule.onNodeWithText(activity.getString(R.string.notes)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.create_note)).assertDoesNotExist()
    }
}