package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.ListEntity

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListMenu(
    items: List<ListEntity>,
    defaultSelectedItem: ListEntity,
    onListSelected: (ListEntity) -> Unit,
    onAddNewListButtonClicked: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        SelectedListTitle(listTitle = defaultSelectedItem.name)

        val sortedItems = items.sortedByDescending { it.timestamp }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortedItems.forEach { item ->
                // menu item
                DropdownMenuItem(onClick = {
                    expanded = false
                    onListSelected(item)
                }) {
                    DropdownMenuItemContent(
                        item = item,
                        isSelected = item.id == defaultSelectedItem.id
                    )
                }
            }

            DropdownMenuItem(onClick = {}) {
                Button(
                    onClick = {
                        expanded = false
                        onAddNewListButtonClicked()
                    }
                ) {
                    Text(text = stringResource(R.string.button_add_new_list))
                }
            }

        }
    }
}

@Composable
fun SelectedListTitle(listTitle: String) {
    Text(
        text = listTitle,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DropdownMenuItemContent(item: ListEntity, isSelected: Boolean) {
    if (isSelected) {
        Text(
            text = item.name,
            color = MaterialTheme.colors.primary
        )
    } else {
        Text(text = item.name)
    }
}