package com.anshmidt.notelist.viewmodel

import android.view.KeyEvent.DispatcherState
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
        val tempList = ListEntity(
            id = 1,
            name = "Sample list",
            inTrash = false,
            timestamp = 0L
        )

//        //temp
//        viewModelScope.launch(Dispatchers.IO) {
//            listRepository.addList(tempList)
//        }

        noteRepository.getNotesInList(tempList.id).onEach { notes ->
            _uiState.value = MainUiState(notes, tempList)
        }.catch {error ->
            _uiState.value = getEmptyMainUiState()
        }.launchIn(viewModelScope)
    }

    private fun getEmptyMainUiState() = MainUiState(
        notes = emptyList(),
        list = ListEntity(
            id = 1,
            name = "",
            inTrash = false,
            timestamp = 0L
        )
    )

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


}