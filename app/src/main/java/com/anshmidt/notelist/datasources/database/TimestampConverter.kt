package com.anshmidt.notelist.datasources.database

import java.text.SimpleDateFormat
import java.util.*

object TimestampConverter {
    fun Long.toHumanReadableDate(): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK)
            val date = Date(this)
            format.format(date)
        } catch (e: Exception) {
            e.toString()
        }
    }
}