package com.examplnewprojecte.note.utils

import com.examplnewprojecte.note.Entity.NoteEntity
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getRelativeDateGroup(createdDate: Long): String {
        val todayStart = getStartOfDay(System.currentTimeMillis()) // 00:00:00 hôm nay
        val createdStart = getStartOfDay(createdDate) // 00:00:00 của ngày ghi chú
        val daysAgo = (todayStart - createdStart) / (1000 * 60 * 60 * 24)

        println("🟢 DEBUG: createdDate=${formatDate(createdDate)}, todayStart=${formatDate(todayStart)}, daysAgo=$daysAgo")

        return when (daysAgo) {
            0L -> "Hôm nay"
            1L -> "Hôm qua"
            2L -> "Hôm kia"
            in 3..6 -> "Trong tuần qua"
            else -> formatDate(createdDate)
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
