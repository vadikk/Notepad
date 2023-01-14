package com.example.notepad.ui.createNote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.R
import com.example.notepad.data.model.Note
import com.example.notepad.data.repository.FolderRepository
import com.example.notepad.data.repository.NoteRepository
import com.example.notepad.data.utils.currentTimeInSeconds
import com.example.notepad.domain.usecase.DataFieldType
import com.example.notepad.domain.usecase.ValidateNoteField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteVM @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle,
    private val validateNoteField: ValidateNoteField,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val uid = CreateNoteFragmentArgs.fromSavedStateHandle(savedStateHandle).noteUid
    private val _createNoteState = MutableStateFlow(CreateNoteState())
    val createNoteState = _createNoteState.asStateFlow()

    var currentNote: Note? = null
        private set

    init {
        viewModelScope.launch {
            currentNote = noteRepository.getNoteById(uid)
            val folder = folderRepository.folderList().filter { it.isSelected }.firstOrNull()
            _createNoteState.emit(
                CreateNoteState(
                    title = currentNote?.title.orEmpty(),
                    description = currentNote?.description.orEmpty(),
                    color = currentNote?.bgColor ?: R.color.white,
                    date = currentNote?.date.orEmpty(),
                    password = currentNote?.password.orEmpty(),
                    isPinned = currentNote?.isPinned ?: false,
                    folder = folder?.uid?.toString().orEmpty()
                )
            )
        }
    }

    fun saveNote(callback: () -> Unit) {
        viewModelScope.launch {
            noteRepository.insert(
                Note(
                    uid = if (currentNote == null) null else currentNote?.uid,
                    date = currentTimeInSeconds().toString(),
                    title = _createNoteState.value.title,
                    description = _createNoteState.value.description,
                    bgColor = _createNoteState.value.color,
                    password = _createNoteState.value.password,
                    folder = _createNoteState.value.folder
                )
            )
            callback()
        }
    }

    fun deleteNote(callback: () -> Unit) {
        if (uid.isEmpty()) callback()
        else {
            viewModelScope.launch {
                noteRepository.deleteNote(uid)
                callback()
            }
        }
    }

    fun changeTitle(newText: String) {
        _createNoteState.value = _createNoteState.value.copy(
            title = newText,
            isApplyBtnEnabled = validateNoteField.validateInputFields(
                newText,
                currentNote?.title.orEmpty(),
                DataFieldType.TITLE
            )
        )
    }

    fun changeDescription(newText: String) {
        _createNoteState.value = _createNoteState.value.copy(
            description = newText,
            isApplyBtnEnabled = validateNoteField.validateInputFields(
                newText,
                currentNote?.description.orEmpty(),
                DataFieldType.DESCRIPTION
            )
        )
    }

    fun passNewColor(color: Int) {
        _createNoteState.value = _createNoteState.value.copy(
            color = color,
            isApplyBtnEnabled = validateNoteField.validateColor(
                color,
                currentNote?.bgColor ?: 0,
                DataFieldType.COLOR
            )
        )
    }

    fun refreshPasswordNote(password: String) {
        _createNoteState.value = _createNoteState.value.copy(
            password = password,
            isApplyBtnEnabled = validateNoteField.validateInputFields(
                password,
                currentNote?.password.orEmpty(),
                DataFieldType.PASSWORD
            )
        )
    }

    fun moveNoteToFolder(uid: String) {
        _createNoteState.value = _createNoteState.value.copy(
            folder = uid,
            isApplyBtnEnabled = validateNoteField.validateInputFields(
                uid,
                currentNote?.folder.orEmpty(),
                DataFieldType.FOLDER
            )
        )
    }

}

data class CreateNoteState(
    val title: String = "",
    val description: String = "",
    val color: Int = R.color.white,
    val date: String = "",
    val password: String = "",
    val isPinned: Boolean = false,
    val folder: String = "",
    val isApplyBtnEnabled: Boolean = false
)