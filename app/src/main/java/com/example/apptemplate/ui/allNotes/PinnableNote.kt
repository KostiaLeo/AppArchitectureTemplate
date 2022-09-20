package com.example.apptemplate.ui.allNotes

import com.example.apptemplate.data.source.local.room.NoteEntity

data class PinnableNote(
    val note: NoteEntity,
    val isPinned: Boolean
)