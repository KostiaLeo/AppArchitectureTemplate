package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val unpinNoteUseCase: UnpinNoteUseCase
) {
    suspend operator fun invoke(noteId: Int) {
        coroutineScope {
            launch { notesRepository.deleteNoteEntityById(noteId) }
            launch { unpinNoteUseCase(noteId) }
        }
    }
}