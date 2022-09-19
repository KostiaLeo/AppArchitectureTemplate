package com.example.apptemplate.data.source.local

import com.example.apptemplate.data.source.NotesDataSource
import com.example.apptemplate.data.source.local.room.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNotesNotesDataSource @Inject constructor(
    private val notesDao: NotesDao
) : NotesDataSource {

}