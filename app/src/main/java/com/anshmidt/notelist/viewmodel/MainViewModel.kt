package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.datasources.database.*
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.ListWithNotesRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.uistate.ListsUiState
import com.anshmidt.notelist.ui.uistate.NotesUiState
import com.anshmidt.notelist.ui.uistate.ScreenMode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel(
    private val noteRepository: NoteRepository,
    private val listRepository: ListRepository,
    private val listWithNotesRepository: ListWithNotesRepository
) : ViewModel() {

    private val _notesUiState = MutableStateFlow(getEmptyNotesUiState())
    val notesUiState: StateFlow<NotesUiState> = _notesUiState.asStateFlow()

    private val _listsUiState = MutableStateFlow(getEmptyListsUiState())
    val listsUiState: StateFlow<ListsUiState> = _listsUiState.asStateFlow()

    private val _screenModeState: MutableStateFlow<ScreenMode> = MutableStateFlow(ScreenMode.View)
    val screenModeState: StateFlow<ScreenMode> = _screenModeState.asStateFlow()

    private val _selectedNoteState: MutableStateFlow<NoteEntity?> = MutableStateFlow(null)
    val selectedNoteState: StateFlow<NoteEntity?> = _selectedNoteState.asStateFlow()


    init {
        displayNotes()
        displayLists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun displayNotes() {
        listRepository.getLastOpenedListId().flatMapLatest { lastOpenedListId ->
            noteRepository.getNotesInList(lastOpenedListId)
        }.onEach { notes ->
            // Do nothing in case of Trash mode
            if (_screenModeState.value !is ScreenMode.Trash) {
                val sortedNotes = notes.sortedByDescending { it.timestamp }
                _notesUiState.value = NotesUiState(
                    notes = sortedNotes
                )
            }
        }.launchIn(viewModelScope + Dispatchers.IO)
    }

    private fun displayNotesInTrash() {
        noteRepository.getAllNotesInTrash().onEach { notesWithListEntity ->
            if (_screenModeState.value == ScreenMode.Trash) {
                val notes = notesWithListEntity.map { noteWithListEntity ->
                    noteWithListEntity.toNoteEntity()
                }
                _notesUiState.value = NotesUiState(notes = notes)
            }
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
        notes = emptyList()
    )

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
            //noteRepository.deleteNote(note)
            noteRepository.moveNoteToTrash(noteId = note.id)
        }
    }

    fun onNoteDismissed(note: NoteEntity) {
        deleteNote(note = note)
    }

    fun onPutBackClicked(selectedNote: NoteEntity?) {
        /**
         * It's possible that list of the note is in trash.
         * In order to avoid that, we mark the note as "not in trash",
         * and we mark the list as "not in trash".
         */
        selectedNote?.let { note ->
            viewModelScope.launch(Dispatchers.IO) {
                noteRepository.removeNoteFromTrash(noteId = note.id)
                listRepository.removeListFromTrash(listId = note.listId)
            }
        }
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

            if (_screenModeState.value is ScreenMode.View ||
                _screenModeState.value is ScreenMode.Edit) {
                _screenModeState.value = ScreenMode.Edit
                _selectedNoteState.value = newNote
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
        // Switch to Edit mode
        _screenModeState.value = ScreenMode.Edit
        _selectedNoteState.value = note
    }

    fun onNoteSelected(note: NoteEntity?) {
        _selectedNoteState.value = note
    }

    fun onDoneIconClicked() {
        // Exit Edit mode, return to View mode
        _screenModeState.value = ScreenMode.View
        _selectedNoteState.value = null
        // Delete empty notes in current list
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteEmptyNotes(listId = _listsUiState.value.selectedList.id)
        }
    }

    fun onUpIconClicked() {
        // Return from Trash mode to View mode
        _screenModeState.value = ScreenMode.View
        displayNotes()
    }

    fun onNoteEdited(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // If the text of the note is edited, we update the timestamp
            val currentTime = System.currentTimeMillis()
            val noteWithUpdatedTime = note.copy(timestamp = currentTime)
            noteRepository.updateNote(noteWithUpdatedTime)

            // If the note is edited, we also update timestamp on the list
            val listWithUpdatedTime = _listsUiState.value.selectedList.copy(timestamp = currentTime)
            listRepository.updateList(listWithUpdatedTime)
        }
    }

    fun onOpenTrashClicked() {
        _screenModeState.value = ScreenMode.Trash
        displayNotesInTrash()
    }

    fun onNoteMovedToAnotherList(selectedList: ListEntity, selectedNote: NoteEntity?) {
        if (selectedNote == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNote = selectedNote.copy(listId = selectedList.id)
            noteRepository.updateNote(updatedNote)
        }
    }

    fun onPriorityChanged(selectedNote: NoteEntity?, priority: Priority) {
        if (selectedNote == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNote = selectedNote.copy(priority = priority)
            noteRepository.updateNote(updatedNote)
        }
    }

    fun onCopyListToClipboardClicked() {
        val selectedList = _listsUiState.value.selectedList
        val notesInSelectedList = _notesUiState.value.notes
        listWithNotesRepository.copyListWithNotesToClipboard(
            list = selectedList,
            notes = notesInSelectedList
        )
    }

    fun onAddNotesFromClipboardClicked() {
        val newNoteTexts = listWithNotesRepository.getNoteTextsFromClipboard()
        if (newNoteTexts.isEmpty()) return

        val selectedListId = _listsUiState.value.selectedList.id

        viewModelScope.launch(Dispatchers.IO) {
            /**
             * We want to show notes from clipboard in the same order they were in string.
             * Since the app displays recent notes first, we add the notes to repository
             * starting with the notes from the end.
             */
            newNoteTexts.reversed().map { noteText ->
                val newNoteWithoutId = NoteEntity(
                    timestamp = System.currentTimeMillis(),
                    text = noteText,
                    listId = selectedListId,
                    inTrash = false
                )
                noteRepository.addNote(newNoteWithoutId)

                /**
                 *  Way to implement animation of adding notes one by one,
                 *  and at the same time make sure that they have different timestamps.
                 */
                delay(200)
            }

            // After the notes are imported, we update timestamp on the list
            val listWithUpdatedTime = _listsUiState.value.selectedList.copy(timestamp = System.currentTimeMillis())
            listRepository.updateList(listWithUpdatedTime)
        }
    }

    companion object {
        val TAG = MainViewModel::class.java.simpleName
    }


}