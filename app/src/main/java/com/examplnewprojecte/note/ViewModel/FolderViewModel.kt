package com.examplnewprojecte.note.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.examplnewprojecte.note.Database.NoteDatabase
import com.examplnewprojecte.note.Entity.FolderEntity
import com.examplnewprojecte.note.Repository.FolderRepository
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val folderDao = NoteDatabase.getDatabase(application).folderDao()
    private val repository = FolderRepository(folderDao)

    val parentFolders: LiveData<List<FolderEntity>> = repository.parentFolders

    fun insert(folder: FolderEntity) = viewModelScope.launch {
        repository.insert(folder)
    }

    fun insert(name: String) = viewModelScope.launch {
        repository.insert(FolderEntity(name = name))
    }

    fun update(folder: FolderEntity) = viewModelScope.launch {
        repository.update(folder)
    }

    fun delete(folder: FolderEntity) = viewModelScope.launch {
        repository.delete(folder)
    }
}