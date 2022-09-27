package com.example.apptemplate.di

import android.content.Context
import androidx.room.Room
import com.example.apptemplate.data.source.local.room.NotesDao
import com.example.apptemplate.data.source.local.room.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseTestModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): NotesDatabase {
        return Room
            .inMemoryDatabaseBuilder(context.applicationContext, NotesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideMainDao(notesDatabase: NotesDatabase): NotesDao {
        return notesDatabase.notesDao
    }
}