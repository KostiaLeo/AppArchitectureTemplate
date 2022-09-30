package com.example.apptemplate

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.semantics.NotesTestTags.noteItemTag
import com.example.apptemplate.semantics.isPinnedKey
import com.example.apptemplate.ui.allNotes.AllNotesScreen
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
@MediumTest
@HiltAndroidTest
class AllNotesScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    AllNotesScreen(
                        onOpenNote = {},
                        onCreateNewNote = {}
                    )
                }
            }
        }
    }

    @Test
    fun testNotesAreDisplayed() {
        val notes = populateRepository(notesAmount = 3)

        setContent()

        composeTestRule.onAllNodesWithTag(noteItemTag)
            .assertCountEquals(notes.size)
            .assertAll(SemanticsMatcher.expectValue(isPinnedKey, false))

        notes.forEach { note ->
            composeTestRule.onNodeWithText(note.title, useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithText(note.text, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun testNotesArePinned() {
        val notes = populateRepository(notesAmount = 5)

        setContent()

        listOf(1, 3).forEach { noteIndex ->
            composeTestRule.onNodeWithText(notes[noteIndex].title).performTouchInput { longClick() }
            composeTestRule.onNodeWithText(activity.getString(R.string.pin_note)).performClick()
            composeTestRule.onNodeWithText(notes[noteIndex].title).assertIsPinned()
        }
    }

    @Test
    fun testNotesAreUnpinned() {
        val notes = populateRepository(notesAmount = 5)
        runBlocking {
            launch { notesPreferencesRepository.pinNote(notes[1].id) }
            launch { notesPreferencesRepository.pinNote(notes[3].id) }
        }
        setContent()

        composeTestRule.onNodeWithText(notes[1].title).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.unpin_note)).performClick()
        composeTestRule.onNodeWithText(notes[1].title).assertIsPinned(false)
        composeTestRule.onNodeWithText(notes[3].title).assertIsPinned()
    }

    @Test
    fun testNoteIsDeleted() {
        val notes = populateRepository(notesAmount = 1)
        setContent()

        composeTestRule.onNodeWithText(notes[0].title).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.delete)).performClick()
        composeTestRule.onNodeWithText(notes[0].title).assertDoesNotExist()
    }

    @Test
    fun testPinnedNoteIsDeleted() {
        val notes = populateRepository(notesAmount = 2)
        runBlocking {
            launch { notesPreferencesRepository.pinNote(notes[1].id) }
        }
        setContent()

        composeTestRule.onNodeWithText(notes[1].title).performTouchInput { longClick() }
        composeTestRule.onNodeWithText(activity.getString(R.string.delete)).performClick()
        composeTestRule.onNodeWithText(notes[1].title).assertDoesNotExist()
        composeTestRule.onNodeWithText(activity.getString(R.string.pinned_notes)).assertDoesNotExist()
    }

    @Test
    fun testAllNotesAreDeleted() {
        val notes = populateRepository(notesAmount = 5)
        runBlocking {
            launch { notesPreferencesRepository.pinNote(notes[1].id) }
            launch { notesPreferencesRepository.pinNote(notes[3].id) }
        }
        setContent()

        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.delete_notes)).performClick()
        composeTestRule.onNodeWithText(activity.getString(R.string.yes)).performClick()
        composeTestRule.onAllNodesWithTag(noteItemTag).assertCountEquals(0)
        composeTestRule.onNodeWithText(activity.getString(R.string.pinned_notes)).assertDoesNotExist()
    }

    private fun populateRepository(notesAmount: Int = 3): List<NoteEntity> {
        return runBlocking {
            repeat(notesAmount) {
                notesRepository.insertNoteEntity(
                    NoteEntity(
                        id = it,
                        title = "Title$it",
                        text = "Text$it",
                        timeCreatedMillis = System.currentTimeMillis() - (notesAmount - it) * 1000
                    )
                )
            }
            notesRepository.notesFlow.first()
        }
    }

    private fun SemanticsNodeInteraction.assertIsPinned(expected: Boolean = true) =
        assert(SemanticsMatcher.expectValue(isPinnedKey, expected))
}