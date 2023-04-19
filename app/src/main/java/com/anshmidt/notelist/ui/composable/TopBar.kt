package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.ListEntity
import com.anshmidt.notelist.ui.ListPreviewProvider
import com.anshmidt.notelist.ui.uistate.ScreenMode


@Composable
fun TopBar(
    lists: List<ListEntity>,
    screenMode: ScreenMode,
    searchQuery: String?,
    @PreviewParameter(ListPreviewProvider::class) selectedList: ListEntity,
    onListSelected: (ListEntity) -> Unit,
    onMoveListToTrashClicked: (ListEntity) -> Unit,
    onAddNewListButtonClicked: () -> Unit,
    onDoneIconClicked: () -> Unit,
    onUpIconInTrashClicked: () -> Unit,
    onUpIconForSearchClicked: () -> Unit,
    onRenameListIconClicked: () -> Unit,
    onOpenTrashClicked: () -> Unit,
    onCopyListToClipboardClicked: () -> Unit,
    onAddNotesFromClipboardClicked: () -> Unit,
    onEmptyTrashClicked: () -> Unit,
    onSearchIconClicked: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        elevation = 0.dp,
        title = {
            TopBarTitle(
                screenMode = screenMode,
                searchQuery = searchQuery,
                lists = lists,
                selectedList = selectedList,
                onListSelected = onListSelected,
                onAddNewListButtonClicked = onAddNewListButtonClicked
            )
        },
        backgroundColor = Color.Transparent,
        navigationIcon = NavigationIconOrNull(
            screenMode = screenMode,
            searchQuery = searchQuery,
            onDoneIconClicked = onDoneIconClicked,
            onUpIconInTrashClicked = onUpIconInTrashClicked,
            onUpIconForSearchClicked = onUpIconForSearchClicked
        ),
        actions = {
            SearchIcon(onSearchIconClicked = onSearchIconClicked)
            MoreIcon(onClick = {
                isMenuExpanded = !isMenuExpanded
            })
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                if (screenMode == ScreenMode.Trash) {
                    MenuItem(
                        icon = Icons.Outlined.PlaylistRemove,
                        text = stringResource(id = R.string.menu_title_empty_trash),
                        onClick = {
                            isMenuExpanded = false
                            onEmptyTrashClicked()
                        }
                    )
                } else {
                    MenuItem(
                        icon = Icons.Outlined.Edit,
                        text = stringResource(R.string.menu_title_rename_list),
                        onClick = {
                            isMenuExpanded = false
                            onRenameListIconClicked()
                        }
                    )
                    MenuItem(
                        icon = Icons.Outlined.DeleteForever,
                        text = stringResource(R.string.menu_title_move_list_to_trash),
                        onClick = {
                            isMenuExpanded = false
                            onMoveListToTrashClicked(selectedList)
                        }
                    )
                    MenuItem(
                        icon = Icons.Outlined.DeleteSweep,
                        text = stringResource(R.string.menu_title_open_trash),
                        onClick = {
                            isMenuExpanded = false
                            onOpenTrashClicked()
                        }
                    )
                    MenuItem(
                        icon = Icons.Outlined.ContentCopy,
                        text = stringResource(R.string.menu_title_copy_list_to_clipboard),
                        onClick = {
                            isMenuExpanded = false
                            onCopyListToClipboardClicked()
                        }
                    )
                    MenuItem(
                        icon = Icons.Outlined.AddToPhotos,
                        text = stringResource(R.string.menu_title_add_notes_from_clipboard),
                        onClick = {
                            isMenuExpanded = false
                            onAddNotesFromClipboardClicked()
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun TopBarTitle(
    screenMode: ScreenMode,
    searchQuery: String?,
    lists: List<ListEntity>,
    selectedList: ListEntity,
    onListSelected: (ListEntity) -> Unit,
    onAddNewListButtonClicked: () -> Unit
) {
    if (searchQuery != null) {
        SearchField()
        return
    }
    when (screenMode) {
        is ScreenMode.View, is ScreenMode.Edit -> {
            ListMenu(
                items = lists,
                defaultSelectedItem = selectedList,
                onListSelected = onListSelected,
                onAddNewListButtonClicked = onAddNewListButtonClicked
            )
        }
        is ScreenMode.Trash -> {
            SelectedListTitle(listTitle = stringResource(id = R.string.trash_screen_title))
        }
    }
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
private fun SearchIcon(onSearchIconClicked: () -> Unit) {
    IconButton(onClick = onSearchIconClicked) {
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
private fun UpIcon(onUpIconClicked: () -> Unit) {
    IconButton(onClick = onUpIconClicked) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
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

private fun NavigationIconOrNull(
    screenMode: ScreenMode,
    searchQuery: String?,
    onDoneIconClicked: () -> Unit,
    onUpIconInTrashClicked: () -> Unit,
    onUpIconForSearchClicked: () -> Unit
): @Composable (() -> Unit)? {
    return if (screenMode is ScreenMode.Edit) {{
        DoneIcon(onDoneIconClicked)
    }} else if (screenMode is ScreenMode.Trash) {{
        UpIcon(onUpIconInTrashClicked)
    }} else if (searchQuery != null) {{
        UpIcon(onUpIconForSearchClicked)
    }} else null
}

@Composable
private fun SearchField() {
    val searchQuery by remember {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }
    SideEffect {
        focusRequester.requestFocus()
    }
    TextField(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp)
            .focusRequester(focusRequester),
        value = searchQuery,
        onValueChange = {}
    )
}