package com.anshmidt.notelist.repository

import com.anshmidt.notelist.datasources.clipboard.ClipboardUtil
import com.anshmidt.notelist.datasources.database.ListEntity
import com.anshmidt.notelist.datasources.database.ListWithNotesConverter
import com.anshmidt.notelist.datasources.database.NoteEntity

class ListWithNotesRepository(
    val clipboardUtil: ClipboardUtil,
    val listWithNotesConverter: ListWithNotesConverter
) {

    fun copyListWithNotesToClipboard(list: ListEntity, notes: List<NoteEntity>) {
        val listAsString = listWithNotesConverter.convertToString(
            list = list,
            notes = notes
        )
        clipboardUtil.copyToClipboard(listAsString)
    }

    fun getNoteTextsFromClipboard(): List<String> {
        val textFromClipboard = clipboardUtil.getTextFromClipboard()
        val noteTexts = listWithNotesConverter.convertStringToNotes(text = textFromClipboard)
        return noteTexts
    }

}