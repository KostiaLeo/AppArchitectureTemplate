package com.example.apptemplate

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.apptemplate.data.preferences.DatastoreNotesPreferencesRepository
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertContentEquals

private const val TEST_DATASTORE_NAME: String = "test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NotesPreferencesRepositoryTest {
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile =
            { testContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )

    private val preferencesRepository: NotesPreferencesRepository =
        DatastoreNotesPreferencesRepository(testDataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun `test pin 1 note`() = runTest {
        val noteId = 1
        preferencesRepository.pinNote(noteId)

        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        val expectedIds = listOf(noteId)
        assertContentEquals(expectedIds, pinnedNotesIds)
    }

    @Test
    fun `test pin 2 notes`() = runTest {
        val notesIds = listOf(1, 2)
        notesIds.forEach {
            preferencesRepository.pinNote(it)
        }
        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        assertContentEquals(notesIds, pinnedNotesIds)
    }

    @Test
    fun `test pin 2 notes and unpin 1`() = runTest {
        val notesIds = listOf(1, 2)
        notesIds.forEach {
            preferencesRepository.pinNote(it)
        }
        preferencesRepository.unpinNote(2)
        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        val expectedFinalPinnedIds = listOf(1)
        assertContentEquals(expectedFinalPinnedIds, pinnedNotesIds)
    }

    @Test
    fun `test pin 3 notes and unpin 3`() = runTest {
        val notesIds = listOf(1, 2, 3)
        notesIds.forEach {
            preferencesRepository.pinNote(it)
        }

        notesIds.forEach {
            preferencesRepository.unpinNote(it)
        }
        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        assertContentEquals(emptyList(), pinnedNotesIds)
    }

    @Test
    fun `test pin same note twice`() = runTest {
        val noteId = 1
        preferencesRepository.pinNote(noteId)
        preferencesRepository.pinNote(noteId)

        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        val expectedIds = listOf(noteId)
        assertContentEquals(expectedIds, pinnedNotesIds)
    }

    @Test
    fun `test unpin not existing note`() = runTest {
        preferencesRepository.unpinNote(1)
        val pinnedNotesIds = preferencesRepository.pinnedNotesIdsFlow.first()
        assertContentEquals(emptyList(), pinnedNotesIds)
    }


    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
        testCoroutineScope.runTest {
            testDataStore.edit { it.clear() }
        }
        testCoroutineScope.cancel()
    }
}