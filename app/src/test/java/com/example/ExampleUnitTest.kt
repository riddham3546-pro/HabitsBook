package com.example

import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dateUtils_getTodayString_isValidFormat() {
        val today = DateUtils.getTodayString()
        assertNotNull(today)
        assertTrue(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun dateUtils_getDaysInMonth_returnsCorrectCount() {
        // October has 31 days
        val days = DateUtils.getDaysInMonth(2026, 9) // 0-based month 9 = October
        assertEquals(31, days.size)
        assertEquals(1, days.first().dayNumber)
        assertEquals(31, days.last().dayNumber)
    }
}
