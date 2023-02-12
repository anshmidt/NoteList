package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.ui.ListPreviewProvider

@Preview
@Composable
fun TopBar(@PreviewParameter(ListPreviewProvider::class) selectedList: ListEntity) {
    var isExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        elevation = 0.dp,
        title = {
            ListMenu()
//            Text(
//                text = selectedList.name,
//                color = MaterialTheme.colors.primary
//            )
        },
        backgroundColor = Color.Transparent,
//        navigationIcon = {
//            IconButton(onClick = {/* Do Something*/ }) {
//                Icon(Icons.Filled.ArrowBack, null)
//            }
//        },
        actions = {
            SearchIcon()
            MoreIcon(onClick = {
                isExpanded = !isExpanded
            })
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                MenuItem(
                    icon = Icons.Outlined.Edit,
                    text = stringResource(R.string.menu_title_rename_list),
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.DeleteForever,
                    text = stringResource(R.string.menu_title_move_list_to_trash),
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.DeleteSweep,
                    text = stringResource(R.string.menu_title_open_trash),
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.ContentCopy,
                    text = stringResource(R.string.menu_title_copy_list_to_clipboard),
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.AddToPhotos,
                    text = stringResource(R.string.menu_title_add_notes_from_clipboard),
                    onClick = {}
                )
            }
        }
    )
}

@Composable
fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    DropdownMenuItem(onClick = onClick) {
        Row() {
            Icon(icon, contentDescription = text)
            Text(text, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun SearchIcon() {
    IconButton(onClick = {/* Do Something*/ }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun MoreIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}