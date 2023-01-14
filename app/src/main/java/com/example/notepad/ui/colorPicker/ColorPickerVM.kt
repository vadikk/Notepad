package com.example.notepad.ui.colorPicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.notepad.R
import com.example.notepad.ui.createNote.CreateNoteFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ColorPickerVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private var colorID = ColorPickerDialogArgs.fromSavedStateHandle(savedStateHandle).colorID
    private val colorList = listOf(
        R.color.white,
        R.color.color1, R.color.color2,
        R.color.color3, R.color.color4,
        R.color.color5, R.color.color6,
        R.color.color7, R.color.color8,
        R.color.color9, R.color.color10
    )
    private val _colorFlow = MutableStateFlow<List<ColorItem>>(emptyList())
    val colorFlow = _colorFlow.asStateFlow()

    init {
        _colorFlow.value = colorList.mapIndexed { index, id ->
            val selectColor = if (colorID == 0) 0 else colorList.indexOf(colorList.first { colorID == it })
            ColorItem(id = index, isChecked = selectColor == index, colorID = id)
        }
    }

}

data class ColorItem(
    val id: Int = -1,
    val isChecked: Boolean = false,
    val colorID: Int = 0
)