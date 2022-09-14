package com.example.apptemplate.di

import com.example.apptemplate.data.repository.DefaultRepository
import com.example.apptemplate.data.repository.Repository
import com.example.apptemplate.data.source.DataSource
import com.example.apptemplate.data.source.local.LocalDataSource
import com.example.apptemplate.data.source.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

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