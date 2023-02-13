package com.anshmidt.notelist.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteListViewModel(
    val noteRepository: NoteRepository,
    val listRepository: ListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(getEmptyMainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        onViewCreated()
    }

    fun onViewCreated() {


//        viewModelScope.launch(Dispatchers.IO) {
//
//            flow {
//                delay(2000)
//                emit(MainUiState(
//                    lists = emptyList(),
//                    selectedList = ListEntity(name = "Updated", inTrash = false, timestamp = 0L),
//                    notes = emptyList()
//                ))
//            }.collect {
//                _uiState.value = it
////                _uiState.value = _uiState.value.copy(notes = listOf(NoteEntity(text = "d", timestamp = 0L, listId = 1, inTrash = false)))
//                Log.d("viewmodel", "Collected")
//            }
//        }


//        viewModelScope.launch(Dispatchers.IO) {
//            listRepository.getLastOpenedList()
//            //noteRepository.getNotesInLastOpenedList()
//                .collect { lastOpenedList ->
//                Log.d("viewmodel", "Creating UiState with $lastOpenedList")
//                _uiState.update { MainUiState(
//                    notes = emptyList(),
//                     selectedList = lastOpenedList,
//                    lists = emptyList()
//                ) }
//                _uiState.value = MainUiState(
//                    notes = emptyList(),
//                    selectedList = lastOpenedList,
////                    selectedList = ListEntity(id = 1, name = "test", inTrash = false, timestamp = 0L),
////                    lists = emptyList()
//                    lists = listOf(
//                        ListEntity(id = 1, name = "testFromLists", inTrash = false, timestamp = 0L),
//                        ListEntity(id = 2, name = "testFromLists2", inTrash = false, timestamp = 0L),
//                        ListEntity(id = 3, name = "testFromLists3", inTrash = false, timestamp = 0L)
//                    )
//                )
//            }
//        }











        viewModelScope.launch(Dispatchers.IO) {
            combine(
                noteRepository.getNotesInLastOpenedList(),
                listRepository.getLastOpenedList(),
                listRepository.getAllLists()
            ) { notes, lastOpenedList, lists ->
                MainUiState(
                    notes = notes,
                    selectedList = lastOpenedList,
                    lists = lists
                )
            }.catch { error ->
                _uiState.value = getEmptyMainUiState()
            }.collect { mainUiState ->
                _uiState.value = mainUiState
            }
        }








    }

    private fun getEmptyMainUiState(): MainUiState {
        Log.d("viewmodel", "Creating UiState with emptyState")
        return MainUiState(
            notes = emptyList(),
            selectedList = ListEntity(
                id = 1,
                name = "",
                inTrash = false,
                timestamp = 0L
            ),
            lists = emptyList()
        )
    }

    private fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note)
        }
    }

    fun onNoteDismissed(note: NoteEntity) {
        deleteNote(note = note)
    }

    fun onAddNoteButtonClicked() {
        addNote()
    }

    private fun addNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val tempText = System.currentTimeMillis().toString()
            noteRepository.addNote(tempText)
        }
    }

    fun onMoveListToTrashClicked(selectedList: ListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepository.deleteList(selectedList)
        }
    }

    fun onAddNewListButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val newList = ListEntity(
                name = "list${System.currentTimeMillis()}",
                timestamp = 0L,
                inTrash = false
            )
            listRepository.addList(newList)
        }
    }

    fun onListOpened(listEntity: ListEntity) {
        _uiState.value = _uiState.value.copy(selectedList = listEntity)
        viewModelScope.launch(Dispatchers.IO) {
            listRepository.saveLastOpenedList(listEntity)
        }
    }


}