package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.MainUiState
import kotlinx.coroutines.flow.*

class NoteListViewModel(
    val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(getEmptyMainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        onViewCreated()
    }

    fun onViewCreated() {
        val tempList = ListEntity(
            id = 0,
            name = "Sample list",
            inTrash = false,
            timestamp = 0L
        )
        noteRepository.getNotesInList(tempList.id).onEach { notes ->
            _uiState.value = MainUiState(notes, tempList)
        }.catch {error ->
            _uiState.value = getEmptyMainUiState()
        }.launchIn(viewModelScope)
    }

    private fun getEmptyMainUiState() = MainUiState(
        notes = emptyList(),
        list = ListEntity(
            id = 0,
            name = "",
            inTrash = false,
            timestamp = 0L
        )
    )


}