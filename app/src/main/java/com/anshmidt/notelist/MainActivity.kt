package com.anshmidt.notelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.ui.theme.NoteListTheme
import com.anshmidt.notelist.viewmodel.NoteListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: NoteListViewModel by viewModel()

        setContent {
            NoteListTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: NoteListViewModel
) {
    //val uiState by viewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxHeight()) {
        Notes(
            notes = uiState.notes,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun Notes(notes: List<NoteEntity>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = notes) {
            Note(noteEntity = it)
            Divider()
        }
    }
}



@Composable
fun Note(noteEntity: NoteEntity) {
    Surface {
        Text(
            text = noteEntity.text,
            modifier = Modifier
                .padding(16.dp)
        )
    }

}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    NoteListTheme {
//        MainScreen()
//    }
//}