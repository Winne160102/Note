package com.examplnewprojecte.note.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getRelativeDateGroup(createdDate: Long): String {
        return try {
            val todayStart = getStartOfDay(System.currentTimeMillis()) // 00:00:00 hôm nay
            val createdStart = getStartOfDay(createdDate) // 00:00:00 của ngày ghi chú
            val daysAgo = (todayStart - createdStart) / (1000 * 60 * 60 * 24)

            println("🟢 DEBUG: createdDate=${formatDate(createdDate)}, todayStart=${formatDate(todayStart)}, daysAgo=$daysAgo")

            when {
                daysAgo < 0 -> "Tương lai" // Xử lý trường hợp thời gian trong tương lai
                daysAgo == 0L -> "Hôm nay"
                daysAgo == 1L -> "Hôm qua"
                daysAgo == 2L -> "Hôm kia"
                daysAgo in 3..6 -> "Trong tuần qua"
                else -> formatDate(createdDate)
            }
        } catch (e: Exception) {
            println("🟢 ERROR: getRelativeDateGroup failed: ${e.message}")
            "Không xác định"
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        return try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        } catch (e: Exception) {
            println("🟢 ERROR: getStartOfDay failed: ${e.message}")
            0L
        }
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) {
            println("🟢 ERROR: formatDate failed: ${e.message}")
            "Không xác định"
        }
    }
}