package com.example.apptemplate.e2e

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.apptemplate.HiltTestActivity
import com.example.apptemplate.R
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.navigation.NotesAppNavHost
import com.example.apptemplate.semantics.NotesTestTags
import com.example.apptemplate.semantics.isPinnedKey
import com.example.apptemplate.utils.findTextField
import com.example.apptemplate.utils.populateRepository
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class NotesAppE2ETest {
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
    fun testCreateTwoNotes_andPinOne() {
        setContent()

        // create 2 notes
        repeat(2) {
            composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note))
                .performClick()
            composeTestRule.findTextField(R.string.type_title_hint).performTextInput("Title$it")
            composeTestRule.findTextField(R.string.type_text_hint).performTextInput("Text$it")
            Espresso.closeSoftKeyboard()
            composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note))
                .performClick()
        }

        // pin note
        composeTestRule.onNodeWithText("Title1").performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.pin_note)).performClick()

        composeTestRule.onNodeWithText("Title1").assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(isPinnedKey, true))
        composeTestRule.onNodeWithText("Title0").assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(isPinnedKey, false))
    }

    @Test
    fun testCreateNoteWithTwoExisting_pinOne_andDeleteAnotherOne() {
        notesRepository.populateRepository(notesAmount = 2)
        setContent()

        // create note
        val inputTitle = "Test title"
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note))
            .performClick()
        composeTestRule.findTextField(R.string.type_title_hint).performTextInput(inputTitle)
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note))
            .performClick()
        composeTestRule.onNodeWithText(inputTitle).assertIsDisplayed()

        val currentNotes = runBlocking { notesRepository.notesFlow.first() }

        // pin note
        composeTestRule.onNodeWithText(currentNotes[1].title).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.pin_note)).performClick()
        composeTestRule.onNodeWithText(currentNotes[1].title).assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(isPinnedKey, true))

        // delete another note
        composeTestRule.onNodeWithText(currentNotes[2].title).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.delete)).performClick()
        composeTestRule.onNodeWithText(currentNotes[2].title).assertDoesNotExist()

        composeTestRule.onNodeWithText(currentNotes[0].title).assertIsDisplayed()
    }

    @Test
    fun testEditNote_andPinIt() {
        val notes: List<NoteEntity> = notesRepository.populateRepository(notesAmount = 2)
        setContent()

        // edit note
        val noteToEdit = notes[1]
        composeTestRule.onNodeWithText(noteToEdit.title).performClick()
        val newTitle = "New title"
        val newText = "New text"
        with(composeTestRule) {
            onNodeWithText(noteToEdit.title).performTextClearance()
            findTextField(R.string.type_title_hint).performTextInput(newTitle)
            onNodeWithText(noteToEdit.text).performTextClearance()
            findTextField(R.string.type_text_hint).performTextInput(newText)
            onNodeWithContentDescription(activity.getString(R.string.save_note))
                .performClick()
        }
        composeTestRule.onNodeWithText(newTitle).assertIsDisplayed()

        // pin note
        composeTestRule.onNodeWithText(newTitle).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.pin_note)).performClick()
        composeTestRule.onNodeWithText(newTitle).assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(isPinnedKey, true))
    }

    @Test
    fun testDeleteAllNotes_createNewOne_andPinIt() {
        val notes: List<NoteEntity> = notesRepository.populateRepository(notesAmount = 5)
        runBlocking {
            launch { notesPreferencesRepository.pinNote(notes[1].id) }
            launch { notesPreferencesRepository.pinNote(notes[3].id) }
        }
        setContent()

        // delete al notes
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.delete_notes))
            .performClick()
        composeTestRule.onNodeWithText(activity.getString(R.string.yes)).performClick()
        composeTestRule.onAllNodesWithTag(NotesTestTags.noteItemTag).assertCountEquals(0)
        composeTestRule.onNodeWithText(activity.getString(R.string.pinned_notes))
            .assertDoesNotExist()

        val inputTitle = "Test title"
        val inputText = "Test text"
        // create new note
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note))
            .performClick()
        composeTestRule.findTextField(R.string.type_title_hint).performTextInput(inputTitle)
        composeTestRule.findTextField(R.string.type_text_hint).performTextInput(inputText)
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.save_note))
            .performClick()

        // pin note
        composeTestRule.onNodeWithText(inputTitle).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.pin_note)).performClick()

        composeTestRule.onNodeWithText(inputTitle).assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(isPinnedKey, true))
        composeTestRule.onNodeWithText(activity.getString(R.string.pinned_notes))
            .assertIsDisplayed()
    }
}