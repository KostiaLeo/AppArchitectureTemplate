package com.example.apptemplate.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val pinnedNotesKeyName = "pinnedNotes"
private val pinnedNotesKey = stringSetPreferencesKey(pinnedNotesKeyName)

interface NotesPreferencesRepository {
    val pinnedNotesIdsFlow: Flow<List<Int>>
    suspend fun pinNote(noteId: Int)
    suspend fun unpinNote(noteId: Int)
    suspend fun clearAllPins()
}

class DatastoreNotesPreferencesRepository constructor(
    private val dataStore: DataStore<Preferences>
): NotesPreferencesRepository {
    override val pinnedNotesIdsFlow: Flow<List<Int>> = dataStore.data.map { prefs ->
        prefs[pinnedNotesKey].orEmpty().mapNotNull { it.toIntOrNull() }
    }

    override suspend fun pinNote(noteId: Int) {
        dataStore.edit { prefs ->
            prefs[pinnedNotesKey] = prefs[pinnedNotesKey].orEmpty() + noteId.toString()
        }
    }

    override suspend fun unpinNote(noteId: Int) {
        dataStore.edit { prefs ->
            prefs[pinnedNotesKey] = prefs[pinnedNotesKey].orEmpty() - noteId.toString()
        }
    }

    override suspend fun clearAllPins() {
        dataStore.edit { prefs ->
            prefs[pinnedNotesKey] = emptySet()
        }
    }
}