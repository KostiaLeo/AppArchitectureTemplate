package com.example.apptemplate.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apptemplate.navigation.NoteRoutes.allNotesRoute
import com.example.apptemplate.navigation.NoteRoutes.noteDetailsRoute
import com.example.apptemplate.navigation.NotesArguments.noteId
import com.example.apptemplate.navigation.NotesScreens.noteDetailsScreen
import com.example.apptemplate.ui.allNotes.AllNotesScreen
import com.example.apptemplate.ui.details.NoteDetailsScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = allNotesRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(
            route = allNotesRoute
        ) {
            AllNotesScreen(
                onOpenNote = { note ->
                    navController.navigate(noteDetailsRoute.replace("{$noteId}", note.id.toString()))
                },
                onCreateNewNote = {
                    navController.navigate(noteDetailsScreen)
                }
            )
        }
        composable(
            route = noteDetailsRoute,
            arguments = listOf(navArgument(noteId) { type = NavType.IntType; defaultValue = -1 })
        ) {
            NoteDetailsScreen(onNavigateUp = navController::navigateUp)
        }
    }
}