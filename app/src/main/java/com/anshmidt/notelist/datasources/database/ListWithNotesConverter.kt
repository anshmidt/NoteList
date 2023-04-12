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

    fun convertStringToNotes(text: String): List<String> {
        if (text.isEmpty()) return emptyList()

        val delimiterType = guessTypeOfDelimiter(text)
        return when (delimiterType) {
            DelimiterType.NewLine -> {
                text.split("\n")
            }
            DelimiterType.NumberWithDot -> {
                text.split("""(\n)?\d+\."""
                    .toRegex())
                    .map { it.trim() }
                    .drop(1)
            }
            DelimiterType.NumberWithRoundBracket -> {
                text.split("""(\n)?\d+\)"""
                    .toRegex())
                    .map { it.trim() }
                    .drop(1)
            }
            DelimiterType.NotFound -> {
                listOf(text)
            }
        }

    }

    private fun guessTypeOfDelimiter(text: String): DelimiterType {
        if (text.contains("1)") && text.contains("2)"))
            return DelimiterType.NumberWithRoundBracket
        if (text.contains("1.") && text.contains("2."))
            return DelimiterType.NumberWithDot
        if (text.contains("\n"))
            return DelimiterType.NewLine
        return DelimiterType.NotFound
    }

    private sealed class DelimiterType {
        object NumberWithDot : DelimiterType()
        object NumberWithRoundBracket : DelimiterType()
        object NewLine : DelimiterType()
        object NotFound : DelimiterType()
    }

}