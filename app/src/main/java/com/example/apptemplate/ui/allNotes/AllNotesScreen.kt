package com.example.apptemplate.ui.allNotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptemplate.R
import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun AllNotesScreen(
    onOpenNote: (note: NoteEntity) -> Unit,
    onCreateNewNote: () -> Unit,
    viewModel: AllNotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    val scaffoldState = rememberBottomSheetScaffoldState()

    var isDeleteAllNotesDialogShown by remember { mutableStateOf(false) }

    if (isDeleteAllNotesDialogShown) {
        AlertDialog(
            onDismissRequest = { isDeleteAllNotesDialogShown = false },
            text = {
                Text(text = "Do you want to delete all notes?")
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteAllNotes()
                    isDeleteAllNotesDialogShown = false
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDeleteAllNotesDialogShown = false
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
            .background(MaterialTheme.colors.background)
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
                    if (uiState.pinnedNotes.isNotEmpty() || uiState.notPinnedNotes.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                isDeleteAllNotesDialogShown = true
                            })
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    if (uiState.pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pinned",
                                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(
                                    top = 12.dp,
                                    bottom = 12.dp,
                                    start = 8.dp
                                )
                            )
                        }

                        itemsIndexed(
                            uiState.pinnedNotes,
                            key = { _: Int, item: PinnableNote -> item.note.id }
                        ) { index, note ->
                            val shape = when {
                                uiState.pinnedNotes.size == 1 -> NoteItemPosition.SINGLE_ITEM
                                index == 0 -> NoteItemPosition.START_OF_LIST
                                index == uiState.pinnedNotes.lastIndex -> NoteItemPosition.END_OF_LIST
                                else -> NoteItemPosition.MIDDLE_OF_LIST
                            }
                            NoteItem(
                                position = shape,
                                pinnableNote = note,
                                onNoteClicked = onOpenNote,
                                onLongPressed = viewModel::onNoteFocused
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 8.dp)
                        )
                    }

                    itemsIndexed(
                        uiState.notPinnedNotes,
                        key = { _: Int, item: PinnableNote -> item.note.id }
                    ) { index, note ->
                        val shape = when {
                            uiState.notPinnedNotes.size == 1 -> NoteItemPosition.SINGLE_ITEM
                            index == 0 -> NoteItemPosition.START_OF_LIST
                            index == uiState.notPinnedNotes.lastIndex -> NoteItemPosition.END_OF_LIST
                            else -> NoteItemPosition.MIDDLE_OF_LIST
                        }
                        NoteItem(
                            position = shape,
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
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable(onClick = onChangePinClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        val text = if (pinnableNote?.isPinned == true) "Unpin" else "Pin"
        Text(text = text, style = MaterialTheme.typography.body1)
    }

    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable(onClick = onDeleteNoteClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = "Delete", style = MaterialTheme.typography.body1)
    }
}

enum class NoteItemPosition {
    START_OF_LIST, MIDDLE_OF_LIST, END_OF_LIST, SINGLE_ITEM
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    position: NoteItemPosition,
    pinnableNote: PinnableNote,
    onNoteClicked: (NoteEntity) -> Unit,
    onLongPressed: (PinnableNote) -> Unit
) {
    val shape = when (position) {
        NoteItemPosition.SINGLE_ITEM -> RoundedCornerShape(size = 8.dp)
        NoteItemPosition.START_OF_LIST -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        NoteItemPosition.MIDDLE_OF_LIST -> RectangleShape
        NoteItemPosition.END_OF_LIST -> RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
    }

    Surface(
        shape = shape,
        color = colorResource(id = R.color.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .combinedClickable(onClick = {
                    onNoteClicked(pinnableNote.note)
                }, onLongClick = {
                    onLongPressed(pinnableNote)
                })
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = pinnableNote.note.title,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = pinnableNote.note.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            if (
                position == NoteItemPosition.START_OF_LIST || position == NoteItemPosition.MIDDLE_OF_LIST
            ) {
                Divider(color = colorResource(id = R.color.onSurfaceVariant))
            }
        }
    }
}