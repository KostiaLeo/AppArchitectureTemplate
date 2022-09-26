package com.example.apptemplate.domain

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import javax.inject.Inject

class ObservePinnedNotesUseCase @Inject constructor(
    private val notesPreferencesRepository: NotesPreferencesRepository
) {
    operator fun invoke() = notesPreferencesRepository.pinnedNotesIdsFlow
}