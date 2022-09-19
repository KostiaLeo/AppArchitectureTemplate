package com.example.apptemplate.data.source.local

import com.example.apptemplate.data.source.DataSource
import com.example.apptemplate.data.source.local.room.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val notesDao: NotesDao
) : DataSource {

}