package com.anshmidt.notelist.datasources.database

import androidx.room.TypeConverter

class PriorityConverter {

    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.value
    }

    @TypeConverter
    fun toPriority(value: Int): Priority {
        return when(value) {
            1 -> Priority.MAJOR
            2 -> Priority.NORMAL
            3 -> Priority.MINOR
            else -> Priority.NORMAL
        }
    }

}