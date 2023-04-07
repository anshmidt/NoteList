package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.ListEntity

@Composable
fun MoveNoteDialog(
    lists: List<ListEntity>,
    onDialogDismissed: () -> Unit = {},
    onListSelected: (ListEntity) -> Unit
) {
    Dialog(
        onDismissRequest = onDialogDismissed,
        content = {
            Surface(
                color = MaterialTheme.colors.background
            ) {
                Column {
                    MoveNoteDialogTitle()
                    MoveNoteDialogContent(
                        lists = lists,
                        onListSelected = onListSelected,
                        modifier = Modifier.weight(1f)
                    )
                    MoveNoteDialogButtons(
                        onDialogDismissed = onDialogDismissed
                    )
                }
            }
        }
    )
}

@Composable
private fun MoveNoteDialogButtons(
    onDialogDismissed: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDialogDismissed) {
            Text(text = stringResource(id = R.string.list_name_dialog_negative_button))
        }
    }
}

@Composable
private fun MoveNoteDialogContent(
    lists: List<ListEntity>,
    onListSelected: (ListEntity) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier) {
        items(
            count = lists.size
        ) { key ->
            Text(
                text = lists[key].name,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clickable {
                        onListSelected(lists[key])
                    }
            )
        }
    }
}

@Composable
private fun MoveNoteDialogTitle() {
    Text(
        text = stringResource(id = R.string.move_note_dialog_title),
        modifier = Modifier.padding(24.dp),
        color = MaterialTheme.colors.primary
    )
}