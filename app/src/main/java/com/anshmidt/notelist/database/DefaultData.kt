package com.anshmidt.notelist.database

import android.content.Context
import com.anshmidt.notelist.R
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity

class DefaultData(context: Context) {

    private val currentTime = System.currentTimeMillis()

    val defaultList = ListEntity(
        id = 1,
        name = context.getString(R.string.default_data_list_name),
        inTrash = false,
        timestamp = currentTime
    )

    val defaultNote = NoteEntity(
        id = 1,
        text = context.getString(R.string.default_data_note_text),
        inTrash = false,
        timestamp = currentTime,
        listId = defaultList.id
    )

}