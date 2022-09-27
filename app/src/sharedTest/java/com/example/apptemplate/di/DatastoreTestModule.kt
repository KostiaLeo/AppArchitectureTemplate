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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatastoreModule::class]
)
object DatastoreTestModule {
    private const val TEST_DATASTORE_NAME: String = "test_datastore"

    @Provides
    @Singleton
    fun provideDatastore(
        @ApplicationContext applicationContext: Context,
        @IODispatcher ioDispatcher: CoroutineDispatcher
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = TestScope(ioDispatcher + Job()),
            produceFile = { applicationContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideNotesPreferencesDataSource(dataStore: DataStore<Preferences>): NotesPreferencesRepository {
        return DatastoreNotesPreferencesRepository(dataStore)
    }
}