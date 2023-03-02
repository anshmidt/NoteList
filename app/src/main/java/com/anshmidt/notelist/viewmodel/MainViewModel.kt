package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.database.DefaultData
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.NotesMode
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
        displayNotes()
        displayLists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun displayNotes() {
        listRepository.getLastOpenedListId().flatMapLatest { lastOpenedListId ->
            noteRepository.getNotesInList(lastOpenedListId)
        }.onEach { notes ->
            val currentMode = _notesUiState.value.mode
            _notesUiState.value = NotesUiState(
                notes = notes,
                mode = currentMode
            )
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

    private fun getEmptyNotesUiState() = NotesUiState(
        notes = emptyList(),
        mode = NotesMode.View
    )

    private fun getEmptyListsUiState() = ListsUiState(
        selectedList = ListEntity(
            id = DefaultData.DEFAULT_SELECTED_LIST_ID,
            name = "",
            inTrash = false,
            timestamp = 0L
        ),
        lists = emptyList(),
        mode = NotesMode.View
    )

    private fun getListsUiState(selectedListId: Int, lists: List<ListEntity>): ListsUiState {
        val selectedList = lists.firstOrNull() { it.id == selectedListId }
        return if (selectedList == null) {
            getEmptyListsUiState()
        } else {
            ListsUiState(
                selectedList = selectedList,
                lists = lists,
                mode = NotesMode.View
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
        val selectedListId = _listsUiState.value.selectedList.id

        val newNoteWithoutId = NoteEntity(
            timestamp = System.currentTimeMillis(),
            text = "",
            listId = selectedListId,
            inTrash = false
        )

        viewModelScope.launch(Dispatchers.IO) {
            val newNoteId = noteRepository.addNote(newNoteWithoutId).toInt()
            val newNote = newNoteWithoutId.copy(id = newNoteId)

            if (_listsUiState.value.mode == NotesMode.View) {
                _notesUiState.value = _notesUiState.value.copy(mode = NotesMode.Edit(focusedNote = newNote))
                _listsUiState.value = _listsUiState.value.copy(mode = NotesMode.Edit(focusedNote = newNote))
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
            val newListWithoutId = ListEntity(
                name = "list${System.currentTimeMillis()}",
                timestamp = 0L,
                inTrash = false
            )
            // id of a new list is autogenerated on adding to repository
            val newListId = listRepository.addList(newListWithoutId).toInt()
            val newList = newListWithoutId.copy(id = newListId)
            listRepository.saveLastOpenedList(newList)
        }
    }

    fun onListOpened(list: ListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepository.saveLastOpenedList(list)
        }
    }

    fun onNoteClicked(note: NoteEntity) {
        // Enter Edit mode
        _notesUiState.value = _notesUiState.value.copy(mode = NotesMode.Edit(focusedNote = note))
        _listsUiState.value = _listsUiState.value.copy(mode = NotesMode.Edit(focusedNote = note))
    }

    fun onNoteLongClicked(note: NoteEntity) {

    }

    fun onDoneIconClicked() {
        // Exit Edit mode, return to View mode
        _notesUiState.value = _notesUiState.value.copy(mode = NotesMode.View)
        _listsUiState.value = _listsUiState.value.copy(mode = NotesMode.View)
    }

    fun onNoteEdited(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    companion object {
        val TAG = MainViewModel::class.java.simpleName
    }


}