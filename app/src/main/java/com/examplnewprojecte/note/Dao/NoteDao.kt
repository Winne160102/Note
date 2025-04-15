package com.examplnewprojecte.note.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.examplnewprojecte.note.Entity.NoteEntity

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Delete
    suspend fun deleteNotes(notes: List<NoteEntity>)

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :query || '%' ORDER BY createdDate DESC")
    fun searchNotes(query: String): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE folderId = :folderId ORDER BY createdDate DESC")
    fun getNotesByFolder(folderId: Int): LiveData<List<NoteEntity>> // Thêm hàm mới
}