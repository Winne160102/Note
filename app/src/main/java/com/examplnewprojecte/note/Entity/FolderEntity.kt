package com.examplnewprojecte.note.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity (
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentId: Long? = null
)