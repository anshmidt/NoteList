package com.anshmidt.notelist.datasources.database

import android.content.Context
import com.anshmidt.notelist.R

enum class Priority(val value: Int) {
    MAJOR(1),
    NORMAL(2),
    MINOR(3)
}

fun Priority.increase() = when(this) {
    Priority.MINOR -> Priority.NORMAL
    Priority.NORMAL -> Priority.MAJOR
    Priority.MAJOR -> this
}

fun Priority.decrease() = when(this) {
    Priority.MINOR -> this
    Priority.NORMAL -> Priority.MINOR
    Priority.MAJOR -> Priority.NORMAL
}

fun Priority.isHigher(priority2: Priority): Boolean {
    return this.value < priority2.value
}

fun Priority.isLower(priority2: Priority): Boolean {
    return this.value > priority2.value
}

fun Priority.convertToString(context: Context): String {
    val stringResourceId = when(this) {
        Priority.MINOR -> R.string.priority_minor
        Priority.NORMAL -> R.string.priority_normal
        Priority.MAJOR -> R.string.priority_major
    }
    return context.getString(stringResourceId)
}