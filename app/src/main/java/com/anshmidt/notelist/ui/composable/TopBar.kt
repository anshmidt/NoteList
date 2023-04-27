package com.anshmidt.notelist.ui.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onAddNewListButtonClicked: () -> Unit,
    navigationCallbacks: NavigationCallbacks,
    menuCallbacks: MenuCallbacks,
    searchCallbacks: SearchCallbacks
) {
    TopAppBar(
        elevation = 0.dp,
        title = {
            TopBarTitle(
                screenMode = screenMode,
                searchQuery = searchQuery,
                lists = lists,
                selectedList = selectedList,
                onListSelected = onListSelected,
                onAddNewListButtonClicked = onAddNewListButtonClicked,
                onSearchQueryChanged = searchCallbacks.onSearchQueryChanged,
                onSearchFieldFocused = searchCallbacks.onSearchFieldFocused
            )
        },
        backgroundColor = Color.Transparent,
        navigationIcon = NavigationIconOrNull(
            screenMode = screenMode,
            searchQuery = searchQuery,
            onDoneIconClicked = navigationCallbacks.onDoneIconClicked,
            onUpIconInTrashClicked = navigationCallbacks.onUpIconInTrashClicked,
            onUpIconForSearchClicked = navigationCallbacks.onUpIconForSearchClicked
        ),
        actions = {
            Menu(
                searchQuery = searchQuery,
                searchCallbacks = searchCallbacks,
                menuItemsData = getMenuItemsData(
                    screenMode = screenMode,
                    selectedList = selectedList,
                    menuCallbacks = menuCallbacks
                )
            )
        }
    )
}

@Composable
private fun getMenuItemsData(
    screenMode: ScreenMode,
    selectedList: ListEntity,
    menuCallbacks: MenuCallbacks
): List<MenuItemData> {
    return if (screenMode == ScreenMode.Trash) {
        listOf(
            MenuItemData(
                icon = Icons.Outlined.PlaylistRemove,
                text = stringResource(id = R.string.menu_title_empty_trash),
                onClick = menuCallbacks.onEmptyTrashClicked
            )
        )
    } else {
        listOf(
            MenuItemData(
                icon = Icons.Outlined.Edit,
                text = stringResource(R.string.menu_title_rename_list),
                onClick = menuCallbacks.onRenameListIconClicked
            ),
            MenuItemData(
                icon = Icons.Outlined.DeleteForever,
                text = stringResource(R.string.menu_title_move_list_to_trash),
                onClick = {
                    menuCallbacks.onMoveListToTrashClicked(selectedList)
                }
            ),
            MenuItemData(
                icon = Icons.Outlined.DeleteSweep,
                text = stringResource(R.string.menu_title_open_trash),
                onClick = menuCallbacks.onOpenTrashClicked
            ),
            MenuItemData(
                icon = Icons.Outlined.ContentCopy,
                text = stringResource(R.string.menu_title_copy_list_to_clipboard),
                onClick = menuCallbacks.onCopyListToClipboardClicked
            ),
            MenuItemData(
                icon = Icons.Outlined.AddToPhotos,
                text = stringResource(R.string.menu_title_add_notes_from_clipboard),
                onClick = menuCallbacks.onAddNotesFromClipboardClicked
            )
        )
    }
}

@Composable
private fun Menu(
    searchQuery: String?,
    searchCallbacks: SearchCallbacks,
    menuItemsData: List<MenuItemData>
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    if (searchQuery == null) {
        SearchIcon(onSearchIconClicked = searchCallbacks.onSearchIconClicked)
        MoreIcon(onClick = {
            isMenuExpanded = !isMenuExpanded
        })
    } else {


        ClearSearchFieldIcon(onClearSearchFieldIconClicked = searchCallbacks.onClearSearchFieldIconClicked)
    }

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false }
    ) {
        for (menuItemData in menuItemsData) {
            MenuItem(
                icon = menuItemData.icon,
                text = menuItemData.text,
                onClick = {
                    isMenuExpanded = false
                    menuItemData.onClick()
                }
            )
        }
    }
}

data class MenuItemData(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopBarTitle(
    screenMode: ScreenMode,
    searchQuery: String?,
    lists: List<ListEntity>,
    selectedList: ListEntity,
    onListSelected: (ListEntity) -> Unit,
    onAddNewListButtonClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchFieldFocused: () -> Unit
) {
    AnimatedContent(
        targetState = searchQuery != null,
        transitionSpec = { getTitleAnimationContentTransform(targetState) }
    ) { isSearchFieldVisible ->
        if (isSearchFieldVisible) {
            SearchField(
                searchQuery = searchQuery.orEmpty(),
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchFieldFocused = onSearchFieldFocused
            )
        } else {
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
private fun ClearSearchFieldIcon(onClearSearchFieldIconClicked: () -> Unit) {
    var angle by remember { mutableStateOf(0f) }
    val angleState by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(
            durationMillis = TopBar.SEARCH_FIELD_APPEARANCE_DURATION,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        angle += 90f
    }
    IconButton(
        onClick = onClearSearchFieldIconClicked
    ) {
        Icon(
            modifier = Modifier.rotate(angleState),
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun getTitleAnimationContentTransform(isSearchFieldVisible: Boolean ): ContentTransform {
    return if (isSearchFieldVisible) {
        ContentTransform(
            targetContentEnter = slideInHorizontally(
                animationSpec = tween(TopBar.SEARCH_FIELD_APPEARANCE_DURATION)
            ) { width -> width },
            initialContentExit = slideOutHorizontally(
                animationSpec = tween(TopBar.SEARCH_FIELD_APPEARANCE_DURATION)
            ) { width -> -width }
        )
    } else {
        ContentTransform(
            targetContentEnter = slideInHorizontally(
                animationSpec = tween(TopBar.SEARCH_FIELD_APPEARANCE_DURATION)
            ) { width -> -width },
            initialContentExit = slideOutHorizontally(
                animationSpec = tween(TopBar.SEARCH_FIELD_APPEARANCE_DURATION)
            ) { width -> width }
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
    return if (searchQuery != null) {{
        UpIcon(onUpIconForSearchClicked)
    }} else if (screenMode is ScreenMode.Edit) {{
        DoneIcon(onDoneIconClicked)
    }} else if (screenMode is ScreenMode.Trash) {{
        UpIcon(onUpIconInTrashClicked)
    }} else null
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchFieldFocused: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    SideEffect {
        focusRequester.requestFocus()
    }
    TextField(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 17.dp)
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background
        ),
        textStyle = LocalTextStyle.current.copy(
            textDecoration = TextDecoration.None,
            fontSize = 18.sp
        ),
        interactionSource = interactionSource,
        value = searchQuery,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_field_hint),
                color = MaterialTheme.colors.primary.copy(alpha = 0.2f)
            )
        },
        onValueChange = onSearchQueryChanged,
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            autoCorrect = false,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        })
    )

    if (interactionSource.collectIsPressedAsState().value) {
        onSearchFieldFocused()
    }
}

data class NavigationCallbacks(
    val onDoneIconClicked: () -> Unit,
    val onUpIconInTrashClicked: () -> Unit,
    val onUpIconForSearchClicked: () -> Unit,
)

data class MenuCallbacks(
    val onRenameListIconClicked: () -> Unit,
    val onMoveListToTrashClicked: (ListEntity) -> Unit,
    val onOpenTrashClicked: () -> Unit,
    val onCopyListToClipboardClicked: () -> Unit,
    val onAddNotesFromClipboardClicked: () -> Unit,
    val onEmptyTrashClicked: () -> Unit
)

data class SearchCallbacks(
    val onSearchIconClicked: () -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onClearSearchFieldIconClicked: () -> Unit,
    val onSearchFieldFocused: () -> Unit
)

object TopBar {
    const val SEARCH_FIELD_APPEARANCE_DURATION = 300
}