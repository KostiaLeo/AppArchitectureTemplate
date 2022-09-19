package com.example.apptemplate.ui.secondary

import androidx.lifecycle.ViewModel
import com.example.apptemplate.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecondaryViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

}