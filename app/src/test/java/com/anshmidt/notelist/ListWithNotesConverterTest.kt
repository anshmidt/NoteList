package com.anshmidt.notelist

import com.anshmidt.notelist.datasources.database.ListEntity
import com.anshmidt.notelist.datasources.database.ListWithNotesConverter
import com.anshmidt.notelist.datasources.database.NoteEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ListWithNotesConverterTest {

    val converter = ListWithNotesConverter()

    @Test
    fun `convertToString happy path`() {
        val list = ListEntity(name = "To buy", timestamp = 0L)
        val notes = listOf(
            NoteEntity(
                timestamp = 0L,
                text = "Bananas",
                listId = 0
            ),
            NoteEntity(
                timestamp = 0L,
                text = "Apples",
                listId = 0
            )
        )

        val expected = "To buy:\n1) Bananas\n2) Apples"
        val actual = converter.convertToString(list = list, notes = notes)
        assertEquals(expected, actual)
    }

}