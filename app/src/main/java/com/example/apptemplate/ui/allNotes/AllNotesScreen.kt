package com.example.apptemplate.ui.allNotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    onOpenNote: (note: NoteEntity) -> Unit,
    onCreateNewNote: () -> Unit,
    viewModel: AllNotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    val scaffoldState = rememberBottomSheetScaffoldState()

    var isDeleteAllDialogShown by remember { mutableStateOf(false) }

    if (isDeleteAllDialogShown) {
        AlertDialog(
            onDismissRequest = { isDeleteAllDialogShown = false },
            text = {
                Text(text = "Do you want to delete all notes?")
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteAllNotes()
                    isDeleteAllDialogShown = false
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDeleteAllDialogShown = false
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.focusedNote) {
        if (uiState.focusedNote != null) {
            scaffoldState.bottomSheetState.expand()
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.isCollapsed) {
        if (scaffoldState.bottomSheetState.isCollapsed) {
            viewModel.onNoteFocused(null)
        }
    }

    BottomSheetScaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    scaffoldState.bottomSheetState.collapse()
                }
            },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Notes")
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            isDeleteAllDialogShown = true
                        })
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(text = "Pinned")
                        Divider()
                    }

                    items(uiState.pinnedNotes, key = { it.note.id }) { note ->
                        NoteItem(
                            pinnableNote = note,
                            onNoteClicked = onOpenNote,
                            onLongPressed = viewModel::onNoteFocused
                        )
                    }

                    item {
                        Text(text = "Notes")
                        Divider()
                    }

                    items(uiState.notPinnedNotes, key = { it.note.id }) { note ->
                        NoteItem(
                            pinnableNote = note,
                            onNoteClicked = onOpenNote,
                            onLongPressed = viewModel::onNoteFocused
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNewNote) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            NoteActionContent(
                pinnableNote = uiState.focusedNote,
                onChangePinClicked = {
                    uiState.focusedNote?.run {
                        if (isPinned) {
                            viewModel.unpinNote(note)
                        } else {
                            viewModel.pinNote(note)
                        }
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                onDeleteNoteClicked = {
                    uiState.focusedNote?.let {
                        viewModel.deleteNote(it.note)
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun NoteActionContent(
    pinnableNote: PinnableNote?,
    onChangePinClicked: () -> Unit,
    onDeleteNoteClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .clickable(onClick = onChangePinClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        val text = if (pinnableNote?.isPinned == true) "Unpin" else "Pin"
        Text(text = text)
    }

    Box(
        modifier = Modifier
            .height(64.dp)
            .clickable(onClick = onDeleteNoteClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = "Delete")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    pinnableNote: PinnableNote,
    onNoteClicked: (NoteEntity) -> Unit,
    onLongPressed: (PinnableNote) -> Unit
) {
    Text(
        text = pinnableNote.note.title, modifier = Modifier
            .height(48.dp)
            .combinedClickable(onClick = {
                onNoteClicked(pinnableNote.note)
            }, onLongClick = {
                onLongPressed(pinnableNote)
            })
    )
}