package com.anshmidt.notelist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.anshmidt.notelist.datasources.database.ListEntity

class ListPreviewProvider : PreviewParameterProvider<ListEntity> {
    override val values: Sequence<ListEntity>
        get() = sequenceOf(
            ListEntity(
            id = 0,
            name = "Sample list",
            inTrash = false,
            timestamp = 1L
        )
        )
}