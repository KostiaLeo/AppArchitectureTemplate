package com.example.apptemplate.di

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.fakesource.FakeNotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NotesRepositoryModule::class]
)
abstract class NotesRepositoryTestModule {

    @Binds
    @Singleton
    abstract fun bindRepository(fakeNotesRepository: FakeNotesRepository): NotesRepository
}