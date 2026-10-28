package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val standardDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun getTodayString(): String {
        return standardDateFormat.format(Date())
    }

    fun getTodayFormatted(): String {
        return SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    fun getMonthYearDisplay(calendar: Calendar = Calendar.getInstance()): String {
        return monthYearFormat.format(calendar.time)
    }

    fun getDaysInMonth(year: Int, monthIndex0Based: Int): List<CalendarDay> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthIndex0Based)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val result = mutableListOf<CalendarDay>()

        for (day in 1..totalDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = standardDateFormat.format(cal.time)
            val dayOfWeek = dayOfWeekFormat.format(cal.time).take(2)
            val weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH)
            result.add(
                CalendarDay(
                    dayNumber = day,
                    dayOfWeek = dayOfWeek,
                    dateString = dateStr,
                    weekNumber = weekOfMonth
                )
            )
        }
        return result
    }

    fun parseDate(dateStr: String): Date? {
        return try {
            standardDateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}

data class CalendarDay(
    val dayNumber: Int,
    val dayOfWeek: String,
    val dateString: String,
    val weekNumber: Int
)
