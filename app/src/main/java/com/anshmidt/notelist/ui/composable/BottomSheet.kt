
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.*
import com.anshmidt.notelist.ui.composable.getNoteTextStyle
import com.anshmidt.notelist.ui.uistate.ScreenMode

@Composable
fun BottomSheet(
    screenMode: ScreenMode,
    selectedNote: NoteEntity?,
    onPutBackClicked: () -> Unit,
    onMoveClicked: () -> Unit,
    onPriorityChanged: (Priority) -> Unit
) {
    // BottomSheet is only displayed if a note is selected
    if (selectedNote == null) return

    if (screenMode is ScreenMode.Trash) {
        BottomSheetInTrashMode(
            onPutBackClicked = onPutBackClicked
        )
    } else {
        BottomSheetInViewMode(
            selectedNote = selectedNote,
            onMoveClicked = onMoveClicked,
            onPriorityChanged = onPriorityChanged
        )
    }
}

@Composable
private fun BottomSheetInViewMode(
    selectedNote: NoteEntity,
    onMoveClicked: () -> Unit,
    onPriorityChanged: (Priority) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        BottomSheetItem(
            icon = Icons.Filled.LowPriority,
            title = stringResource(R.string.priority_title_bottom_sheet),
            content = {
                Spacer(Modifier.weight(1f))
                PrioritySelector(
                    currentPriority = selectedNote.priority,
                    onPriorityChanged = onPriorityChanged
                )
            }
        )

        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.16f))

        BottomSheetItem(
            icon = Icons.Filled.ArrowForward,
            title = stringResource(R.string.move_title_bottom_sheet),
            content = {},
            onClick = onMoveClicked
        )
    }
}

@Composable
private fun BottomSheetInTrashMode(onPutBackClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
    ) {
        BottomSheetItem(
            icon = Icons.Filled.Undo,
            title = stringResource(R.string.put_back_bottom_sheet),
            content = {},
            onClick = onPutBackClicked
        )
    }
}

@Composable
private fun BottomSheetItem(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(color = MaterialTheme.colors.background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 20.sp
        )
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PrioritySelector(
    currentPriority: Priority,
    onPriorityChanged: (Priority) -> Unit
) {
    var priority by remember {
        mutableStateOf(currentPriority)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.width(170.dp)
    ) {
        PrioritySelectorIcon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            onClick = {
                priority = priority.decrease()
                onPriorityChanged(priority)
            }
        )
        AnimatedContent(
            targetState = priority,
            transitionSpec = {
                if (initialState.isHigher(targetState)) {
                    ContentTransform(
                        targetContentEnter = slideInVertically { height -> height },
                        initialContentExit = slideOutVertically { height -> -height }
                    )
                } else {
                    ContentTransform(
                        targetContentEnter = slideInVertically { height -> -height },
                        initialContentExit = slideOutVertically { height -> height }
                    )
                }
            }
        ) { targetState ->
            Text(
                text = targetState.convertToString(LocalContext.current),
                style = getNoteTextStyle(priority = priority),
                fontSize = 20.sp
            )
        }

        PrioritySelectorIcon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            onClick = {
                priority = priority.increase()
                onPriorityChanged(priority)
            }
        )
    }
}

@Composable
private fun PrioritySelectorIcon(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(12.dp)
        )
    }
}
