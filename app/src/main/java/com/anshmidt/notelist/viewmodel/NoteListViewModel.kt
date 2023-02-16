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

    private fun onViewCreated() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                noteRepository.getNotesInLastOpenedList(),
                listRepository.getLastOpenedList(),
                listRepository.getAllLists()
            ) { notes, lastOpenedList, lists ->
                    Log.d(TAG, "displaying notes in list $lastOpenedList")
                    if (lastOpenedList == null) {
                        getEmptyMainUiState()
                    } else {
                        MainUiState(
                            notes = notes,
                            selectedList = lastOpenedList,
                            lists = lists
                        )
                    }
                }
                .collect {
                    _uiState.value = it
                }
        }
    }

    private fun getEmptyMainUiState(): MainUiState {
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
            listRepository.getLastOpenedListId().first { lastOpenedListId ->
                noteRepository.addNote(
                    listId = lastOpenedListId
                )
                return@first true
            }
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
//        viewModelScope.launch(Dispatchers.IO) {
//            listRepository.saveLastOpenedList(listEntity)
//        }

        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.getNotesInList(listEntity.id).collect { notes ->
                val lists = _uiState.value.lists
                val selectedList = listEntity
                Log.d(TAG, "list opened: $selectedList")
                _uiState.value = MainUiState(
                    notes = notes,
                    selectedList = selectedList,
                    lists = lists
                )
                listRepository.saveLastOpenedList(listEntity)
            }
        }
    }

    companion object {
        val TAG = NoteListViewModel::class.java.simpleName
    }


}