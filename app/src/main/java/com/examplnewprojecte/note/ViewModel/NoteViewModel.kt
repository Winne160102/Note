package com.examplnewprojecte.note.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.examplnewprojecte.note.Database.NoteDatabase
import com.examplnewprojecte.note.Entity.NoteEntity
import com.examplnewprojecte.note.Repo.NoteRepository
import com.examplnewprojecte.note.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<NoteEntity>>

    private val _groupedNotes = MutableLiveData<Map<String, List<NoteEntity>>>()
    val groupedNotes: LiveData<Map<String, List<NoteEntity>>> get() = _groupedNotes

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
    }

    // Trả về LiveData để Fragment có thể quan sát
    fun getNotesByFolder(folderId: Int): LiveData<List<NoteEntity>> {
        return repository.getNotesByFolder(folderId)
    }

    fun insert(note: NoteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: NoteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteNotes(notes: List<NoteEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotes(notes)
    }

    fun groupNotesByDate(notes: List<NoteEntity>): Map<String, List<NoteEntity>> {
        if (notes.isEmpty()) return emptyMap()

        return try {
            notes.filter { it.createdDate > 0 }
                .groupBy { note ->
                    try {
                        DateUtils.getRelativeDateGroup(note.createdDate) ?: "Không xác định"
                    } catch (e: Exception) {
                        println("🟢 ERROR: groupNotesByDate failed for note ${note.id}: ${e.message}")
                        "Không xác định"
                    }
                }
        } catch (e: Exception) {
            println("🟢 ERROR: groupNotesByDate failed: ${e.message}")
            emptyMap()
        }
    }
}