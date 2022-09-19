package com.example.apptemplate.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.apptemplate.data.preferences.DatastoreNotesPreferencesRepository
import com.example.apptemplate.data.preferences.NotesPreferencesRepository
import com.example.apptemplate.data.repository.RoomNotesRepository
import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.NotesDataSource
import com.example.apptemplate.data.source.local.LocalNotesNotesDataSource
import com.example.apptemplate.data.source.local.room.NotesDao
import com.example.apptemplate.data.source.local.room.NotesDatabase
import com.example.apptemplate.data.source.remote.RemoteNotesDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindRepository(defaultRepository: RoomNotesRepository): NotesRepository

    @Binds
    @LocalSource
    abstract fun bindLocalDataSource(localNotesDataSource: LocalNotesNotesDataSource): NotesDataSource

    @Binds
    @RemoteSource
    abstract fun bindRemoteDataSource(remoteDataSource: RemoteNotesDataSource): NotesDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NotesDatabase::class.java,
            "Notes.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMainDao(notesDatabase: NotesDatabase): NotesDao {
        return notesDatabase.notesDao
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            applicationContext.preferencesDataStoreFile("notesPrefs")
        }
    }

    @Provides
    @Singleton
    fun provideNotesPreferencesDataSource(dataStore: DataStore<Preferences>): NotesPreferencesRepository {
        return DatastoreNotesPreferencesRepository(dataStore)
    }
}
