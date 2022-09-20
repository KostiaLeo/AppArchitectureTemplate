package com.example.apptemplate.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import com.example.apptemplate.domain.CreateNoteUseCase
import com.example.apptemplate.domain.EditNoteUseCase
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
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(NoteDetailsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val noteId: Int? = savedStateHandle.get<Int>(NotesArguments.noteId)?.takeUnless { it == -1 }

    private var currentNote: NoteEntity? = null

    init {
        noteId?.let(::loadNote)
    }

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            val noteResult = notesRepository.getNoteEntityById(id)
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
                    it.copy(errorMessage = "Oops, Something went wrong")
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
    val errorMessage: String? = null
)