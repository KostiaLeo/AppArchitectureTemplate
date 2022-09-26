package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    operator fun invoke() = notesRepository.notesFlow
}