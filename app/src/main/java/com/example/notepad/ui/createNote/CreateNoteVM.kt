package com.example.notepad.ui.createNote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.notepad.R
import com.example.notepad.data.model.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.data.utils.currentTimeInSeconds
import com.example.notepad.domain.usecase.DataFieldType
import com.example.notepad.domain.usecase.ValidateNoteField
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteVM @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle,
    private val validateNoteField: ValidateNoteField,
    private val folderRepository: FolderRepository
) : BaseVM<CreateNoteState, NoteEvent, NoteEffect>() {

    private val uid = CreateNoteFragmentArgs.fromSavedStateHandle(savedStateHandle).noteUid

    var currentNote: Note? = null
        private set

    init {
        viewModelScope.launch {
            currentNote = noteRepository.getNoteById(uid)
            val folder = folderRepository.folderList().filter { it.isSelected }.firstOrNull()
            setState {
                CreateNoteState(
                    title = currentNote?.title.orEmpty(),
                    description = currentNote?.description.orEmpty(),
                    color = currentNote?.bgColor ?: R.color.white,
                    date = currentNote?.date.orEmpty(),
                    password = currentNote?.password.orEmpty(),
                    isPinned = currentNote?.isPinned ?: false,
                    folder = folder?.uid?.toString().orEmpty()
                )
            }
        }
    }

    override fun createInitialState(): CreateNoteState = CreateNoteState()

    override fun handleEvents(event: NoteEvent) {
        when(event) {
            is NoteEvent.ChangeTitle -> changeTitle(event.title)
            is NoteEvent.ChangeDescription -> changeDescription(event.description)
            is NoteEvent.ChangeColor -> passNewColor(event.color)
            is NoteEvent.ChangePassword -> refreshPasswordNote(event.password)
            is NoteEvent.Folder -> moveNoteToFolder(event.uid)
            is NoteEvent.Save -> saveNote()
            is NoteEvent.Delete -> deleteNote()
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            noteRepository.insert(
                Note(
                    uid = if (currentNote == null) null else currentNote?.uid,
                    date = currentTimeInSeconds().toString(),
                    title = currentState.title,
                    description = currentState.description,
                    bgColor = currentState.color,
                    password = currentState.password,
                    folder = currentState.folder
                )
            )
            setEffect { NoteEffect.CloseScreen }
        }
    }

    private fun deleteNote() {
        if (uid.isEmpty()) setEffect { NoteEffect.CloseScreen }
        else {
            viewModelScope.launch {
                noteRepository.deleteNote(uid)
                setEffect { NoteEffect.CloseScreen }
            }
        }
    }

    private fun changeTitle(newText: String) {
        setState {
            this.copy(
                title = newText,
                isApplyBtnEnabled = validateNoteField.validateInputFields(
                    newText,
                    currentNote?.title.orEmpty(),
                    DataFieldType.TITLE
                )
            )
        }
    }

    private fun changeDescription(newText: String) {
        setState {
            this.copy(
                description = newText,
                isApplyBtnEnabled = validateNoteField.validateInputFields(
                    newText,
                    currentNote?.description.orEmpty(),
                    DataFieldType.DESCRIPTION
                )
            )
        }
    }

    private fun passNewColor(color: Int) {
        setState {
            this.copy(
                color = color,
                isApplyBtnEnabled = validateNoteField.validateColor(
                    color,
                    currentNote?.bgColor ?: 0,
                    DataFieldType.COLOR
                )
            )
        }
    }

    private fun refreshPasswordNote(password: String) {
        setState {
            this.copy(
                password = password,
                isApplyBtnEnabled = validateNoteField.validateInputFields(
                    password,
                    currentNote?.password.orEmpty(),
                    DataFieldType.PASSWORD
                )
            )
        }
    }

    private fun moveNoteToFolder(uid: String) {
        setState {
            this.copy(
                folder = uid,
                isApplyBtnEnabled = validateNoteField.validateInputFields(
                    uid,
                    currentNote?.folder.orEmpty(),
                    DataFieldType.FOLDER
                )
            )
        }
    }

}