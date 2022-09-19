package com.example.apptemplate.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.apptemplate.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        val param = savedStateHandle.get<String>("param")
        println(param)
    }
}