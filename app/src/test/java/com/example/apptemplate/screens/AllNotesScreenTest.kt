package com.example.apptemplate.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.apptemplate.HiltTestActivity
import com.example.apptemplate.R
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.ui.allNotes.AllNotesScreen
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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

        // GIVEN - On the "All Notes" screen.
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
    fun `test create note fab is displayed`() {
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.create_note))
            .assertIsDisplayed()
    }
}