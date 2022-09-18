package com.example.apptemplate.data.source.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "main")
data class MainEntity(
    @PrimaryKey val id: String,
    val info: String = ""
)