package com.examplnewprojecte.note.Repo

import androidx.lifecycle.LiveData
import com.examplnewprojecte.note.Dao.NoteDao
import com.examplnewprojecte.note.Entity.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }

    suspend fun deleteNotes(notes: List<NoteEntity>) {
        noteDao.deleteNotes(notes)
    }

    fun searchNotes(query: String): LiveData<List<NoteEntity>> {
        return noteDao.searchNotes(query)
    }

    fun getNotesByFolder(folderId: Int): LiveData<List<NoteEntity>> {
        return noteDao.getNotesByFolder(folderId)
    }
}