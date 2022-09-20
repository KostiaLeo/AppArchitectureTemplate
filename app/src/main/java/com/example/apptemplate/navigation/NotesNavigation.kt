package com.example.apptemplate.navigation

import androidx.navigation.NavController
import com.example.apptemplate.navigation.NotesArguments.noteId
import com.example.apptemplate.navigation.NotesScreens.allNotesScreen
import com.example.apptemplate.navigation.NotesScreens.noteDetailsScreen

object NotesScreens {
    const val allNotesScreen = "all_notes"
    const val noteDetailsScreen = "note_details"
}

object NotesArguments {
    const val noteId = "note_id"
}

object NoteRoutes {
    const val allNotesRoute = allNotesScreen
    const val noteDetailsRoute = "$noteDetailsScreen?$noteId={$noteId}"
}