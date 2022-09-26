package com.example.apptemplate.data.preferences

import kotlinx.coroutines.flow.Flow

interface NotesPreferencesRepository {
    val pinnedNotesIdsFlow: Flow<Set<Int>>
    suspend fun pinNote(noteId: Int)
    suspend fun unpinNote(noteId: Int)
    suspend fun clearAllPins()
}