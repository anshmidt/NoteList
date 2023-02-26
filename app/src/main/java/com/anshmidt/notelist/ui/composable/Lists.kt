package com.anshmidt.notelist.ui.composable

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.anshmidt.notelist.R
import com.anshmidt.notelist.database.ListEntity

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

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                // menu item
                DropdownMenuItem(onClick = {
                    expanded = false
                    onListSelected(item)
                }) {
                    Text(text = item.name)
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
private fun SelectedListTitle(listTitle: String) {
    TextField(
        value = listTitle,
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}