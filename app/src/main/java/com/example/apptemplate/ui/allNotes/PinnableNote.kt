package com.example.apptemplate.ui.allNotes

import com.example.apptemplate.data.source.local.room.NoteEntity
import javax.annotation.concurrent.Immutable

@Immutable
data class PinnableNote(
    val note: NoteEntity,
    val isPinned: Boolean
)