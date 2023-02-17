package com.anshmidt.notelist.database

import android.content.Context
import com.anshmidt.notelist.R

class DefaultData(context: Context) {

    private val currentTime = System.currentTimeMillis()

    val defaultList = ListEntity(
        id = DEFAULT_SELECTED_LIST_ID,
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

    companion object {
        const val DEFAULT_SELECTED_LIST_ID = 1
    }

}