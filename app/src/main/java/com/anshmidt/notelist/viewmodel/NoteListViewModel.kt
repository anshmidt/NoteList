package com.anshmidt.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.ui.MainUiState
import kotlinx.coroutines.flow.*

class NoteListViewModel(
    val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        onViewCreated()
    }

    fun onViewCreated() {
        noteRepository.getNotesInList(0).onEach { notes ->
            _uiState.value = MainUiState(notes)
        }.catch {error ->
            _uiState.value = MainUiState(emptyList())
        }.launchIn(viewModelScope)
//        _uiState.value = MainUiState(notes = )
//            noteRepository.getNotesInList(0)
    }

//    fun loadNotes(): Flow<MainUiState> = flow {
//        noteRepository.getNotesInList(0).collect() { notes ->
//            val uiState = MainUiState(notes)
//            emit(uiState)
//        }
//    }

}