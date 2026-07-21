package com.obrockmole.kmpbetterdining.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateTime {
    val timeZone = TimeZone.of("America/New_York")

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
}