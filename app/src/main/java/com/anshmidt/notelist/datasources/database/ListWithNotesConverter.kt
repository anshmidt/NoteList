package com.anshmidt.notelist.datasources.database

class ListWithNotesConverter {

    fun convertToString(list: ListEntity, notes: List<NoteEntity>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(list.name)
        stringBuilder.append(":\n")

        for (i in 0..notes.size - 1) {
            stringBuilder.append("${i + 1}) ")
            val noteText = notes[i].text
            val noteTextNormalized = noteText.replace("\n", " ")
            stringBuilder.append(noteTextNormalized)
            if (i < notes.size - 1) {
                stringBuilder.append("\n")
            }
        }

        return stringBuilder.toString()
    }

}