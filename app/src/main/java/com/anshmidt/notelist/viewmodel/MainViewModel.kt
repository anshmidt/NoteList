package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.database.DefaultData
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.uistate.ListsUiState
import com.anshmidt.notelist.ui.uistate.NotesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    val noteRepository: NoteRepository,
    val listRepository: ListRepository
) : ViewModel() {

    private val _notesUiState = MutableStateFlow(getEmptyNotesUiState())
    val notesUiState: StateFlow<NotesUiState> = _notesUiState.asStateFlow()

    private val _listsUiState = MutableStateFlow(getEmptyListsUiState())
    val listsUiState: StateFlow<ListsUiState> = _listsUiState.asStateFlow()

    init {
        onViewCreated()
    }

    private fun onViewCreated() {
        displayNotes()
        displayLists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun displayNotes() {
        listRepository.getLastOpenedListId().flatMapLatest { lastOpenedListId ->
            noteRepository.getNotesInList(lastOpenedListId)
        }.onEach { notes ->
            _notesUiState.value = NotesUiState(notes = notes)
        }.launchIn(viewModelScope + Dispatchers.IO)
    }

    private fun displayLists() {
        combine(
            listRepository.getLastOpenedListId(),
            listRepository.getAllLists()
        ) { lastOpenedListId, lists ->
            getListsUiState(
                selectedListId = lastOpenedListId,
                lists = lists
            )
        }.onEach { listsUiState ->
            _listsUiState.value = listsUiState
        }.launchIn(viewModelScope + Dispatchers.IO)
    }

    private fun getEmptyNotesUiState() = NotesUiState(notes = emptyList())

    private fun getEmptyListsUiState() = ListsUiState(
        selectedList = ListEntity(
            id = DefaultData.DEFAULT_SELECTED_LIST_ID,
            name = "",
            inTrash = false,
            timestamp = 0L
        ),
        lists = emptyList()
    )

    private fun getListsUiState(selectedListId: Int, lists: List<ListEntity>): ListsUiState {
        val selectedList = lists.firstOrNull() { it.id == selectedListId }
        return if (selectedList == null) {
            getEmptyListsUiState()
        } else {
            ListsUiState(
                selectedList = selectedList,
                lists = lists
            )
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            listRepository.saveLastOpenedList(listEntity)
        }
    }

    companion object {
        val TAG = MainViewModel::class.java.simpleName
    }


}