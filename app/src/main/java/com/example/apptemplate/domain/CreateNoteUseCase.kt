package com.example.apptemplate.domain

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(title: String, text: String) {
        val entity = NoteEntity(
            title = title,
            text = text
        )
        notesRepository.insertNoteEntity(entity)
    }
}