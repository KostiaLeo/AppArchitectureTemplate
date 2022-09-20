package com.example.apptemplate.ui.allNotes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.DeleteAllNotesUseCase
import com.example.apptemplate.domain.DeleteNoteUseCase
import com.example.apptemplate.domain.PinNoteUseCase
import com.example.apptemplate.domain.UnpinNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    notesRepository: NotesRepository,
    notesPreferencesRepository: NotesPreferencesRepository,
    private val pinNoteUseCase: PinNoteUseCase,
    private val unpinNoteUseCase: UnpinNoteUseCase,
    private val deleteAllNotesUseCase: DeleteAllNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var focusedNote: PinnableNote? by mutableStateOf(null)

    val uiStateFlow: StateFlow<AllNotesUiState> = combine(
        notesRepository.notesFlow,
        notesPreferencesRepository.pinnedNotesIdsFlow,
        snapshotFlow { focusedNote }
    ) { notes, pinnedNotesIds, focusedNote ->
        val pinnableNotes = notes.map { PinnableNote(it, pinnedNotesIds.contains(it.id)) }
        val pinned = pinnableNotes.filter { it.isPinned }
        val notPinned = pinnableNotes.filterNot { it.isPinned }
        AllNotesUiState(pinned, notPinned, focusedNote)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AllNotesUiState()
    )

    fun pinNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            pinNoteUseCase(noteEntity.id)
        }
    }

    fun unpinNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            unpinNoteUseCase(noteEntity.id)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            deleteAllNotesUseCase()
        }
    }

    fun deleteNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            deleteNoteUseCase(noteEntity.id)
        }
    }

    fun onNoteFocused(pinnableNote: PinnableNote?) {
        focusedNote = pinnableNote
    }
}

data class AllNotesUiState(
    val pinnedNotes: List<PinnableNote> = emptyList(),
    val notPinnedNotes: List<PinnableNote> = emptyList(),
    val focusedNote: PinnableNote? = null
)