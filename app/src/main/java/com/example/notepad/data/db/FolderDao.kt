package com.example.notepad.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notepad.data.db.models.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folderEntity: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folderEntity: List<FolderEntity>)

    @Query("DELETE FROM folder WHERE uid LIKE :uid")
    suspend fun deleteFolder(uid: String)

    @Query("SELECT * FROM folder")
    fun getFolders(): Flow<List<FolderEntity>>

    @Query("UPDATE folder SET isSelected =:isSelected WHERE uid LIKE :uid")
    suspend fun selectFolder(uid: String, isSelected: Boolean)

    @Query("SELECT * FROM folder")
    suspend fun folderList(): List<FolderEntity>
}