package com.example.apptemplate.fakesource

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeNotesPreferencesRepository(
    initialPins: Set<Int> = HashSet()
) : NotesPreferencesRepository {

    private val _pinnedNotesIdsFlow = MutableStateFlow(initialPins)

    override val pinnedNotesIdsFlow: Flow<Set<Int>>
    get() = _pinnedNotesIdsFlow

    override suspend fun pinNote(noteId: Int) {
        _pinnedNotesIdsFlow.update {
            it + noteId
        }
    }

    override suspend fun unpinNote(noteId: Int) {
        _pinnedNotesIdsFlow.update {
            it - noteId
        }
    }

    override suspend fun clearAllPins() {
        _pinnedNotesIdsFlow.update {
            emptySet()
        }
    }
}