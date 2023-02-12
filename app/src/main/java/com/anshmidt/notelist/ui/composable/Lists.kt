package com.anshmidt.notelist.ui.composable

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListMenu() {
    val listItems = arrayOf("Favorites", "Options", "Settings", "Share",
        "asdfsd", "Favorites", "Options", "Settings", "Share", "Favorites", "Options", "Settings", "Share",
        "Favorites", "Options", "Settings", "Share","Favorites", "Options", "Settings", "Share",)

    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = selectedItem,
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

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listItems.forEach { selectedOption ->
                // menu item
                DropdownMenuItem(onClick = {
                    selectedItem = selectedOption
                    expanded = false
                }) {
                    Text(text = selectedOption)
                }
            }

            DropdownMenuItem(onClick = {
                expanded = false
            }) {
                Button(
                    onClick = {}
                ) {
                    Text(text = "Add new list")
                }
            }

        }
    }
}