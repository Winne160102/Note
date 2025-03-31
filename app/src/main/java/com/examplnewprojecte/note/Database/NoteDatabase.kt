package com.examplnewprojecte.note.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.examplnewprojecte.note.Dao.NoteDao
import com.examplnewprojecte.note.Entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 2) // Tăng version lên 2
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // Thêm dòng này để xóa database cũ
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
