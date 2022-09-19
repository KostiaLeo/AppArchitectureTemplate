package com.example.apptemplate.domain

import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import javax.inject.Inject

class PinNoteUseCase @Inject constructor(
    private val notesPreferencesRepository: NotesPreferencesRepository
) {
    suspend operator fun invoke(noteId: Int) {
        notesPreferencesRepository.pinNote(noteId)
    }
}