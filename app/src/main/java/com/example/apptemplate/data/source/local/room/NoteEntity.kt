package com.example.apptemplate.data.source.local.room

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Stable
@Entity(tableName = "notes")
data class NoteEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "created") val timeCreatedMillis: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_edited") val timeLastEditedMillis: Long = timeCreatedMillis,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)