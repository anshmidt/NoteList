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
            NoteEntity("Bananas"),
            NoteEntity("Apples")
        )

        val expected = "To buy:\n1) Bananas\n2) Apples"
        val actual = converter.convertToString(list = list, notes = notes)
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes empty string`() {
        val text = ""
        val actual = converter.convertStringToNotes(text)
        val expected = emptyList<String>()
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes newLine delimiters`() {
        val text = "first\nsecond\nthird\nfourth"
        val actual = converter.convertStringToNotes(text)
        val expected = listOf(
            "first",
            "second",
            "third",
            "fourth"
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes number with round bracket delimiters`() {
        val text = "1) First 2)Second\n8) Third\n789)Fourth"
        val actual = converter.convertStringToNotes(text)
        val expected = listOf(
            "First",
            "Second",
            "Third",
            "Fourth"
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes number with dot delimiters`() {
        val text = "1. First 2.Second\n8. Third\n789.Fourth"
        val actual = converter.convertStringToNotes(text)
        val expected = listOf(
            "First",
            "Second",
            "Third",
            "Fourth"
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes no delimiters`() {
        val text = "First Second  Third, 12, Fourth"
        val actual = converter.convertStringToNotes(text)
        val expected = listOf(
            "First Second  Third, 12, Fourth"
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `convertStringToNotes string before first delimiter`() {
        val text = "Title 1. First 2.Second"
        val actual = converter.convertStringToNotes(text)
        val expected = listOf(
            "First",
            "Second"
        )
        assertEquals(expected, actual)
    }

}