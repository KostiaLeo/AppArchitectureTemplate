package com.example.apptemplate.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MainEntity::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract val mainDao: MainDao
}