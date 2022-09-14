package com.example.apptemplate.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IODispatcher