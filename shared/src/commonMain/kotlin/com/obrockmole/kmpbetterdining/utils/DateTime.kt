package com.obrockmole.kmpbetterdining.utils

import kotlinx.datetime.*
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.Instant

object DateTime {
    val timeZone = TimeZone.of("America/New_York")

    val dayOfWeekFormat = LocalDate.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    }

    val monthDayFormat = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        day(padding = Padding.NONE)
    }

    val shortTimeFormat = LocalTime.Format {
        hour()
        char(':')
        minute()
    }

    val longTimeFormat = LocalTime.Format {
        amPmHour(padding = Padding.NONE)
        char(':')
        minute(padding = Padding.ZERO)
        char(' ')
        amPmMarker(am = "AM", pm = "PM")
    }

    fun getInstant(): Instant {
        return Clock.System.now()
    }

    fun getLocalDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(timeZone)
    }

    fun getDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(timeZone).date
    }

    fun getTime(): LocalTime {
        return Clock.System.now().toLocalDateTime(timeZone).time
    }

    fun parseTime(time: String): LocalTime {
        return Instant.parse(time).toLocalDateTime(timeZone).time
    }

    fun parseDate(time: String): LocalDate {
        return Instant.parse(time).toLocalDateTime(timeZone).date
    }

    fun parseDateTime(time: String): LocalDateTime {
        return Instant.parse(time).toLocalDateTime(timeZone)
    }
}