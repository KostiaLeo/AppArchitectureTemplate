package com.example.apptemplate.ui.allNotes

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import androidx.core.os.bundleOf
import com.example.apptemplate.data.source.local.room.NoteEntity

data class PinnableNote(
    val note: NoteEntity,
    val isPinned: Boolean
)
