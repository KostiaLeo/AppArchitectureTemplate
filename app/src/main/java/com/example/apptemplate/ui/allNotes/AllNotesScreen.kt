package com.example.apptemplate.ui.allNotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
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
import androidx.compose.ui.res.stringResource
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
    val scope = rememberCoroutineScope()
    var isDeleteAllNotesDialogShown by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        topBar = {
            AllNotesAppBar(
                onDeleteAllNotesClicked = { isDeleteAllNotesDialogShown = true },
                uiState = uiState
            )
        },
        content = { paddingValues ->
            AllNotesContent(paddingValues, uiState, onOpenNote, viewModel::onNoteFocused)
        },
        floatingActionButton = {
            CreateNoteFAB(onCreateNewNote)
        },
        sheetContent = {
            AllNotesBottomSheetContent(uiState, viewModel, scaffoldState)
        },
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
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp
    )

    OpenBottomSheetOnNoteFocused(uiState, scaffoldState)

    ClearFocusedNoteOnBottomSheetCollapsed(scaffoldState, viewModel)

    if (isDeleteAllNotesDialogShown) {
        AlertDialog(
            onDismissRequest = { isDeleteAllNotesDialogShown = false },
            text = {
                Text(text = stringResource(R.string.delete_all_notes_confirmation))
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteAllNotes()
                    isDeleteAllNotesDialogShown = false
                }) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDeleteAllNotesDialogShown = false
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun AllNotesAppBar(
    onDeleteAllNotesClicked: () -> Unit,
    uiState: AllNotesUiState
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.notes))
        },
        actions = {
            if (uiState.pinnedNotes.isNotEmpty() || uiState.notPinnedNotes.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onDeleteAllNotesClicked)
                )
            }
        }
    )
}

@Composable
private fun AllNotesContent(
    paddingValues: PaddingValues,
    uiState: AllNotesUiState,
    onOpenNote: (NoteEntity) -> Unit,
    onNoteFocused: (PinnableNote) -> Unit
) {
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
                    NotesSectionTitle(text = stringResource(R.string.pinned_notes))
                }

                itemsIndexed(
                    uiState.pinnedNotes,
                    key = { _: Int, item: PinnableNote -> item.note.id }
                ) { index, note ->
                    val noteItemPosition = getNoteItemPosition(uiState.pinnedNotes, index)
                    NoteItem(
                        position = noteItemPosition,
                        pinnableNote = note,
                        onNoteClicked = onOpenNote,
                        onLongPressed = onNoteFocused
                    )
                }
            }

            item {
                NotesSectionTitle(text = stringResource(R.string.notes))
            }

            itemsIndexed(
                uiState.notPinnedNotes,
                key = { _: Int, item: PinnableNote -> item.note.id }
            ) { index, note ->
                val noteItemPosition = getNoteItemPosition(uiState.notPinnedNotes, index)
                NoteItem(
                    position = noteItemPosition,
                    pinnableNote = note,
                    onNoteClicked = onOpenNote,
                    onLongPressed = onNoteFocused
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.NoteItem(
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
            .animateItemPlacement()
    ) {
        Column(
            Modifier
                .combinedClickable(
                    onClick = {
                        onNoteClicked(pinnableNote.note)
                    }, onLongClick = {
                        onLongPressed(pinnableNote)
                    }
                )
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

@Composable
private fun NotesSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(
            top = 12.dp,
            bottom = 12.dp,
            start = 8.dp
        )
    )
}

@Composable
private fun CreateNoteFAB(onCreateNewNote: () -> Unit) {
    FloatingActionButton(onClick = onCreateNewNote) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AllNotesBottomSheetContent(
    uiState: AllNotesUiState,
    viewModel: AllNotesViewModel,
    scaffoldState: BottomSheetScaffoldState
) {
    val scope = rememberCoroutineScope()

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
            .clickable(onClick = onChangePinClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        val textId = if (pinnableNote?.isPinned == true) R.string.unpin_note else R.string.pin_note
        Text(
            text = stringResource(textId),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .clickable(onClick = onDeleteNoteClicked),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(R.string.delete),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ClearFocusedNoteOnBottomSheetCollapsed(
    scaffoldState: BottomSheetScaffoldState,
    viewModel: AllNotesViewModel
) {
    LaunchedEffect(scaffoldState.bottomSheetState.isCollapsed) {
        if (scaffoldState.bottomSheetState.isCollapsed) {
            viewModel.onNoteFocused(null)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OpenBottomSheetOnNoteFocused(
    uiState: AllNotesUiState,
    scaffoldState: BottomSheetScaffoldState
) {
    LaunchedEffect(uiState.focusedNote) {
        if (uiState.focusedNote != null) {
            scaffoldState.bottomSheetState.expand()
        }
    }
}

private enum class NoteItemPosition {
    START_OF_LIST, MIDDLE_OF_LIST, END_OF_LIST, SINGLE_ITEM
}

@Composable
private fun getNoteItemPosition(
    notesList: List<PinnableNote>,
    index: Int
) = when {
    notesList.size == 1 -> NoteItemPosition.SINGLE_ITEM
    index == 0 -> NoteItemPosition.START_OF_LIST
    index == notesList.lastIndex -> NoteItemPosition.END_OF_LIST
    else -> NoteItemPosition.MIDDLE_OF_LIST
}