package com.example.apptemplate.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {

    @Query("SELECT * FROM main")
    fun observeEntities(): Flow<List<MainEntity>>

    @Query("SELECT * FROM main")
    suspend fun getEntities(): List<MainEntity>

    @Query("SELECT * FROM main WHERE id = :id")
    suspend fun getEntityById(id: String): MainEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: MainEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntities(entities: List<MainEntity>)

    @Query("DELETE FROM main")
    suspend fun deleteAllEntities()

    @Query("DELETE FROM main WHERE id = :id")
    suspend fun deleteEntityById(id: String)

    @Update
    suspend fun updateEntity(entity: MainEntity)

    @Query("UPDATE main SET info = :info WHERE id = :id")
    suspend fun setInfoForEntityWithId(info: String, id: String)
}