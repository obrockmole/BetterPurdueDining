package com.obrockmole.kmpbetterdining.utils

import kotlinx.datetime.*
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.Instant

object DateTime {
    val purdueTimeZone = TimeZone.of("America/New_York")
    val systemTimeZone = TimeZone.currentSystemDefault()

    val dayOfWeekFormat = LocalDate.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    }

    val dayOfWeekFormatLong = LocalDate.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        day()
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

    fun getLocalDateTime(timezone: TimeZone = purdueTimeZone): LocalDateTime {
        return Clock.System.now().toLocalDateTime(timezone)
    }

    fun getDate(timezone: TimeZone = purdueTimeZone): LocalDate {
        return Clock.System.now().toLocalDateTime(timezone).date
    }

    fun getTime(timezone: TimeZone = purdueTimeZone): LocalTime {
        return Clock.System.now().toLocalDateTime(timezone).time
    }

    fun parseTime(time: String, timezone: TimeZone = purdueTimeZone): LocalTime {
        return Instant.parse(time).toLocalDateTime(timezone).time
    }

    fun parseDate(date: String, timezone: TimeZone = purdueTimeZone): LocalDate {
        return Instant.parse(date).toLocalDateTime(timezone).date
    }

    fun parseDateTime(dateTime: String, timezone: TimeZone = purdueTimeZone): LocalDateTime {
        return Instant.parse(dateTime).toLocalDateTime(timezone)
    }
}