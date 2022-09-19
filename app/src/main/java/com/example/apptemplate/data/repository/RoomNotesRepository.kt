package com.example.apptemplate.data.repository

import com.example.apptemplate.data.source.DataSource
import com.example.apptemplate.di.IODispatcher
import com.example.apptemplate.di.LocalSource
import com.example.apptemplate.di.RemoteSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomNotesRepository @Inject constructor(
    @LocalSource private val localSource: DataSource,
    @RemoteSource private val remoteSource: DataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : NotesRepository {

}