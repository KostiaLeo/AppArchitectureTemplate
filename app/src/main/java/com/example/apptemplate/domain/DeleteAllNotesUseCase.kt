package com.example.apptemplate.domain

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAllNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val notesPreferencesRepository: NotesPreferencesRepository
) {
    suspend operator fun invoke() {
        coroutineScope {
            launch { notesRepository.deleteAllNoteEntities() }
            launch { notesPreferencesRepository.clearAllPins() }
        }
    }
}