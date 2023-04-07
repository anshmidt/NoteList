package com.anshmidt.notelist.repository

import com.anshmidt.notelist.datasources.clipboard.ClipboardUtil
import com.anshmidt.notelist.datasources.database.ListEntity
import com.anshmidt.notelist.datasources.database.NoteEntity

class ListWithNotesRepository(val clipboardUtil: ClipboardUtil) {

    fun copyListWithNotesToClipboard(list: ListEntity, notes: List<NoteEntity>) {

    }

}