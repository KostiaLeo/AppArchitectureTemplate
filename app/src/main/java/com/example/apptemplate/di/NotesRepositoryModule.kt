package com.example.apptemplate.di

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.repository.RoomNotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(defaultRepository: RoomNotesRepository): NotesRepository
}