package com.example.apptemplate.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptemplate.R
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.CreateNoteUseCase
import com.example.apptemplate.domain.EditNoteUseCase
import com.example.apptemplate.domain.GetNoteUseCase
import com.example.apptemplate.navigation.NotesArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val editNoteUseCase: EditNoteUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: Int? = savedStateHandle.get<Int>(NotesArguments.noteId)?.takeUnless { it == -1 }

    private val _uiStateFlow = MutableStateFlow(NoteDetailsUiState(isNewNote = noteId == null))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private var currentNote: NoteEntity? = null

    init {
        noteId?.let(::loadNote)
    }

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            val noteResult = getNoteUseCase(id)
            noteResult.fold({ note ->
                currentNote = note
                _uiStateFlow.update {
                    it.copy(
                        title = note.title,
                        text = note.text
                    )
                }
            }, {
                _uiStateFlow.update {
                    it.copy(errorMessage = R.string.generic_error_message)
                }
            })

        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiStateFlow.update {
            it.copy(title = newTitle)
        }
    }

    fun onTextChanged(newText: String) {
        _uiStateFlow.update {
            it.copy(text = newText)
        }
    }

    fun saveNote() {
        val state = uiStateFlow.value
        if (state.title.isEmpty() && state.text.isEmpty()) {
            _uiStateFlow.update {
                it.copy(errorMessage = R.string.empty_note_error)
            }
            return
        }
        if (currentNote != null) {
            updateExistingNote()
        } else {
            createNewNote()
        }
    }

    private fun updateExistingNote() {
        viewModelScope.launch {
            val uiState = uiStateFlow.value
            editNoteUseCase(currentNote!!, uiState.title, uiState.text)
            _uiStateFlow.update {
                it.copy(isNoteSaved = true)
            }
        }
    }

    private fun createNewNote() {
        viewModelScope.launch {
            val uiState = uiStateFlow.value
            createNoteUseCase(uiState.title, uiState.text)
            _uiStateFlow.update {
                it.copy(isNoteSaved = true)
            }
        }
    }

    fun onErrorMessageShown() {
        _uiStateFlow.update {
            it.copy(errorMessage = null)
        }
    }
}

data class NoteDetailsUiState(
    val title: String = "",
    val text: String = "",
    val isNoteSaved: Boolean = false,
    val errorMessage: Int? = null,
    val isNewNote: Boolean = false
)