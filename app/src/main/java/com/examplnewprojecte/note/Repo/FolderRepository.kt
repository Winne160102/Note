package com.examplnewprojecte.note.Repository

import androidx.lifecycle.LiveData
import com.examplnewprojecte.note.Dao.FolderDao
import com.examplnewprojecte.note.Entity.FolderEntity

class FolderRepository(private val folderDao: FolderDao) {
    val parentFolders: LiveData<List<FolderEntity>> = folderDao.getParentFolders()

    suspend fun insert(folder: FolderEntity) {
        folderDao.insert(folder)
    }

    suspend fun update(folder: FolderEntity) {
        folderDao.update(folder)
    }

    suspend fun delete(folder: FolderEntity) {
        folderDao.delete(folder)
    }

    fun getSubFolders(parentId: Int): LiveData<List<FolderEntity>> {
        return folderDao.getSubFolders(parentId)
    }

    fun getFolderById(folderId: Int): LiveData<FolderEntity?> {
        return folderDao.getFolderById(folderId)
    }
}