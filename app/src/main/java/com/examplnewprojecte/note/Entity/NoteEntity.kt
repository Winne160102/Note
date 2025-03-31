package com.examplnewprojecte.note.Entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val createdDate: Long = System.currentTimeMillis()
) {
    @Ignore
    var isSelected: Boolean = false
}
