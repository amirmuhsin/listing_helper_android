package com.amirmuhsin.listinghelper.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Time {

    // ISO-8601 in UTC: safe to store as TEXT in Room
    private val ISO_INSTANT: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    // Default UI pattern (device locale & zone)
    private const val DEFAULT_DISPLAY_PATTERN = "dd MMM yyyy, HH:mm"

    /** Now in UTC as Instant */
    fun nowUtc(): Instant = Instant.now()

    /** Now in UTC as ISO-8601 string, e.g. 2025-08-11T14:32:05Z */
    fun nowUtcIso(): String = ISO_INSTANT.format(nowUtc())

    /** Convert stored ISO-8601 UTC string -> device-local formatted text */
    fun isoUtcToDeviceText(
        isoUtc: String?,
        pattern: String = DEFAULT_DISPLAY_PATTERN,
        locale: Locale = Locale.getDefault()
    ): String {
        if (isoUtc.isNullOrBlank()) return ""
        val instant = runCatching { Instant.parse(isoUtc) }.getOrNull() ?: return ""
        return instantToDeviceText(instant, pattern, locale)
    }

    /** Convert Instant -> device-local formatted text */
    fun instantToDeviceText(
        instant: Instant,
        pattern: String = DEFAULT_DISPLAY_PATTERN,
        locale: Locale = Locale.getDefault()
    ): String {
        val zone = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return formatter.format(instant.atZone(zone))
    }

    /** Safe parser: ISO string -> Instant? */
    fun parseIso(isoUtc: String?): Instant? =
        runCatching { Instant.parse(isoUtc) }.getOrNull()

    /** Helpers if you ever need raw epoch millis */
    fun nowEpochMillis(): Long = nowUtc().toEpochMilli()
    fun epochMillisToIsoUtc(ms: Long): String = ISO_INSTANT.format(Instant.ofEpochMilli(ms))
}
