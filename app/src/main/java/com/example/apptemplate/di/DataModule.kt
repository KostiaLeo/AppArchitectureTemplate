package com.example.apptemplate.di

import android.content.Context
import androidx.room.Room
import com.example.apptemplate.data.repository.DefaultRepository
import com.example.apptemplate.data.repository.Repository
import com.example.apptemplate.data.source.DataSource
import com.example.apptemplate.data.source.local.LocalDataSource
import com.example.apptemplate.data.source.local.room.MainDao
import com.example.apptemplate.data.source.local.room.MainDatabase
import com.example.apptemplate.data.source.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindRepository(defaultRepository: DefaultRepository): Repository

    @Binds
    @LocalSource
    abstract fun bindLocalDataSource(localDataSource: LocalDataSource): DataSource

    @Binds
    @RemoteSource
    abstract fun bindRemoteDataSource(remoteDataSource: RemoteDataSource): DataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): MainDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MainDatabase::class.java,
            "Main.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMainDao(mainDatabase: MainDatabase): MainDao {
        return mainDatabase.mainDao
    }
}
