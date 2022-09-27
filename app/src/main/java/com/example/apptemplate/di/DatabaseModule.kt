package com.example.apptemplate.di

import android.content.Context
import androidx.room.Room
import com.example.apptemplate.data.source.local.room.NotesDao
import com.example.apptemplate.data.source.local.room.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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