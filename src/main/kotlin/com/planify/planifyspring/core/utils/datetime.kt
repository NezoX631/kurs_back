package com.planify.planifyspring.core.utils

import java.time.*

fun <T> validateRange(start: T?, end: T?): Boolean = (start != null && end == null) || (start == null && end != null)

fun Instant.atStartOfDay(): Instant =
    this.atZone(ZoneId.of("UTC"))
        .toLocalDate()
        .atStartOfDay(ZoneId.of("UTC"))
        .toInstant()


fun LocalDate.atStartOfDayInstant(): Instant =
    this.atStartOfDay(ZoneId.of("UTC"))
        .toInstant()


fun LocalDate.atEndOfDayInstant(): Instant =
    this.atTime(LocalTime.MAX)
        .atZone(ZoneOffset.UTC)
        .toInstant()


fun LocalDateTime.asUTCInstant(): Instant =
    this.atZone(ZoneOffset.UTC).toInstant()

fun Instant.atStartOfAnHour(): Instant {
    return this.atZone(ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0).toInstant()
}
