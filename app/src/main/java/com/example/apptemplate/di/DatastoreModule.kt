package com.example.apptemplate.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.apptemplate.data.preferences.DatastoreNotesPreferencesRepository
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    private const val DATASTORE_FILE_NAME = "notesPrefs"

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            applicationContext.preferencesDataStoreFile(DATASTORE_FILE_NAME)
        }
    }

    @Provides
    @Singleton
    fun provideNotesPreferencesDataSource(dataStore: DataStore<Preferences>): NotesPreferencesRepository {
        return DatastoreNotesPreferencesRepository(dataStore)
    }
}