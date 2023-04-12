package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.R

@Preview
@Composable
fun ListNameDialog(
    currentListName: String? = null, // null is used when new list is added
    onDialogDismissed: () -> Unit = {},
    onListRenamed: (String) -> Unit = {}
) {
    var listName by remember {
        mutableStateOf(currentListName.orEmpty())
    }
    var isPositiveButtonEnabled by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = onDialogDismissed,
        buttons = { ListNameDialogButtons(
            onPositiveButtonClicked = {
                onListRenamed(listName)
                onDialogDismissed()
            },
            onNegativeButtonClicked = {
                onDialogDismissed()
            },
            isPositiveButtonEnabled = isPositiveButtonEnabled
        ) },
        title = null,
        text = { ListNameDialogContent(
            listName = listName,
            onTextFieldValueChanged = { newValue ->
                listName = newValue
                isPositiveButtonEnabled = listName.isNotEmpty()
            }
        ) }
    )
}

@Composable
private fun ListNameDialogButtons(
    onPositiveButtonClicked: () -> Unit,
    onNegativeButtonClicked: () -> Unit,
    isPositiveButtonEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onNegativeButtonClicked) {
            Text(text = stringResource(id = R.string.list_name_dialog_negative_button))
        }
        TextButton(
            onClick = onPositiveButtonClicked,
            enabled = isPositiveButtonEnabled
        ) {
            Text(text = stringResource(id = R.string.list_name_dialog_positive_button))
        }
    }
}

@Composable
private fun ListNameDialogContent(
    listName: String,
    onTextFieldValueChanged: (String) -> Unit
) {
    Column {
        ListNameDialogTitle()

        val focusRequester = remember { FocusRequester() }
        SideEffect {
            focusRequester.requestFocus()
        }
        TextField(
            value = listName,
            onValueChange = onTextFieldValueChanged,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
            ),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            ),
            modifier = Modifier.focusRequester(focusRequester)
        )
    }
}

@Composable
private fun ListNameDialogTitle() {
    Text(
        text = stringResource(id = R.string.list_name_dialog_title)
    )
}