package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.database.DefaultData
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.uistate.EditMode
import com.anshmidt.notelist.ui.uistate.ListsUiState
import com.anshmidt.notelist.ui.uistate.NotesUiState
import com.anshmidt.notelist.ui.uistate.ScreenMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    private val noteRepository: NoteRepository,
    private val listRepository: ListRepository
) : ViewModel() {

    private val _notesUiState = MutableStateFlow(getEmptyNotesUiState())
    val notesUiState: StateFlow<NotesUiState> = _notesUiState.asStateFlow()

    private val _listsUiState = MutableStateFlow(getEmptyListsUiState())
    val listsUiState: StateFlow<ListsUiState> = _listsUiState.asStateFlow()

    private val _screenModeState: MutableStateFlow<ScreenMode> = MutableStateFlow(ScreenMode.Normal)
    val screenModeState: StateFlow<ScreenMode> = _screenModeState.asStateFlow()

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
        mode = EditMode.View
    )

    private fun getEmptyListsUiState() = ListsUiState(
        selectedList = ListEntity(
            id = DefaultData.DEFAULT_SELECTED_LIST_ID,
            name = "",
            inTrash = false,
            timestamp = 0L
        ),
        lists = emptyList(),
        mode = EditMode.View
    )

    private fun getListsUiState(selectedListId: Int, lists: List<ListEntity>): ListsUiState {
        val selectedList = lists.firstOrNull() { it.id == selectedListId }
        return if (selectedList == null) {
            getEmptyListsUiState()
        } else {
            ListsUiState(
                selectedList = selectedList,
                lists = lists,
                mode = EditMode.View
            )
        }
    }

    private fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            //noteRepository.deleteNote(note)
            noteRepository.moveNoteToTrash(noteId = note.id)
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

            if (_listsUiState.value.mode == EditMode.View) {
                _notesUiState.value = _notesUiState.value.copy(mode = EditMode.Edit(focusedNote = newNote))
                _listsUiState.value = _listsUiState.value.copy(mode = EditMode.Edit(focusedNote = newNote))
            }
        }
    }

    fun onMoveListToTrashClicked(selectedList: ListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // Currently selected list is getting removed, so we should open another list
            listRepository.getAnyOtherListId(listId = selectedList.id).first { newSelectedListId ->
                listRepository.saveLastOpenedList(newSelectedListId)
                //noteRepository.deleteAllNotesFromList(selectedList.id)
                noteRepository.moveToTrashAllNotesFromList(listId = selectedList.id)
                //listRepository.deleteList(selectedList)
                listRepository.moveListToTrash(listId = selectedList.id)
                return@first true
            }
        }
    }

    fun onNewListNameEntered(newListName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newListWithoutId = ListEntity(
                name = newListName,
                timestamp = 0L,
                inTrash = false
            )
            // id of a new list is autogenerated on adding to repository
            val newListId = listRepository.addList(newListWithoutId).toInt()
            val newList = newListWithoutId.copy(id = newListId)
            listRepository.saveLastOpenedList(newList)
        }
    }

    fun onListRenamed(newListName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newList = _listsUiState.value.selectedList.copy(name = newListName)
            listRepository.updateList(newList)
        }
    }

    fun onListOpened(list: ListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepository.saveLastOpenedList(list)
        }
    }

    fun onNoteClicked(note: NoteEntity) {
        // Enter Edit mode
        _notesUiState.value = _notesUiState.value.copy(mode = EditMode.Edit(focusedNote = note))
        _listsUiState.value = _listsUiState.value.copy(mode = EditMode.Edit(focusedNote = note))
    }

    fun onDoneIconClicked() {
        // Exit Edit mode, return to View mode
        _notesUiState.value = _notesUiState.value.copy(mode = EditMode.View)
        _listsUiState.value = _listsUiState.value.copy(mode = EditMode.View)
    }

    fun onUpIconClicked() {
        // Return to Normal mode
        _screenModeState.value = ScreenMode.Normal
    }

    fun onNoteEdited(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    fun onOpenTrashClicked() {
        _screenModeState.value = ScreenMode.Trash
        // Exit Edit mode if needed
        _notesUiState.value = _notesUiState.value.copy(mode = EditMode.View)
        _listsUiState.value = _listsUiState.value.copy(mode = EditMode.View)
    }

    companion object {
        val TAG = MainViewModel::class.java.simpleName
    }


}