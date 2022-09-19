package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import javax.inject.Inject

class EditNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(currentNote: NoteEntity, title: String, text: String) {
        val editedNote = currentNote.copy(
            title = title,
            text = text,
            timeLastEditedMillis = System.currentTimeMillis()
        )
        notesRepository.updateNoteEntity(editedNote)
    }
}