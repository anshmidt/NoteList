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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.ui.ListPreviewProvider
import com.anshmidt.notelist.ui.NotesMode


@Composable
fun TopBar(
    lists: List<ListEntity>,
    mode: NotesMode,
    @PreviewParameter(ListPreviewProvider::class) selectedList: ListEntity,
    onListSelected: (ListEntity) -> Unit,
    onMoveListToTrashClicked: (ListEntity) -> Unit,
    onAddNewListButtonClicked: () -> Unit,
    onDoneIconClicked: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        elevation = 0.dp,
        title = {
            ListMenu(
                items = lists,
                defaultSelectedItem = selectedList,
                onListSelected = onListSelected,
                onAddNewListButtonClicked = onAddNewListButtonClicked
            )
        },
        backgroundColor = Color.Transparent,
        navigationIcon = if (mode is NotesMode.Edit) {{
            DoneIcon(onDoneIconClicked)
        }} else null,
        actions = {
            SearchIcon()
            MoreIcon(onClick = {
                isMenuExpanded = !isMenuExpanded
            })
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                MenuItem(
                    icon = Icons.Outlined.Edit,
                    text = stringResource(R.string.menu_title_rename_list),
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.DeleteForever,
                    text = stringResource(R.string.menu_title_move_list_to_trash),
                    onClick = {
                        onMoveListToTrashClicked(selectedList)
                    }
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
private fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    DropdownMenuItem(onClick = onClick) {
        Row() {
            Icon(icon, contentDescription = text)
            Text(text, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun SearchIcon() {
    IconButton(onClick = {/* Do Something*/ }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun DoneIcon(onDoneIconClicked: () -> Unit) {
    IconButton(onClick = onDoneIconClicked) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun MoreIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}