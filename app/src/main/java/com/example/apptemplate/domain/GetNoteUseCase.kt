package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(id: Int) = notesRepository.getNoteEntityById(id)
}