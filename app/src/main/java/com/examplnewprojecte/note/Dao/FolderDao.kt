package com.examplnewprojecte.note.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.examplnewprojecte.note.Entity.FolderEntity

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: FolderEntity): Long

    @Update
    suspend fun update(folder: FolderEntity)

    @Delete
    suspend fun delete(folder: FolderEntity)

    @Query("SELECT * FROM folders WHERE parentId IS NULL")
    fun getParentFolders(): LiveData<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE parentId = :parentId")
    fun getSubFolders(parentId: Long): LiveData<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :folderId LIMIT 1")
    fun getFolderById(folderId: Int): LiveData<FolderEntity?>
}