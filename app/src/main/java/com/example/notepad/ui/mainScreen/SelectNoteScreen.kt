package com.example.notepad.ui.mainScreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notepad.ui.component.NoteTopBar
import com.example.notepad.ui.component.NotesGrid

@Composable
fun SelectNoteScreen(
    modifier: Modifier = Modifier,
    openDetailNote: (uid: Int) -> Unit,
    openPasswordScreen: (uid: Int, password: String) -> Unit,
    openFolderScreen: () -> Unit
) {

    val mainScreenVM: MainScreenVM = mainViewModel(TypeScreen.MAIN)
    val mainScreenState by mainScreenVM.viewState.collectAsStateWithLifecycle()

    var spanCount by rememberSaveable { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary.copy(0.2F),
                elevation = 0.dp,
                contentPadding = WindowInsets.systemBars
                    .only(WindowInsetsSides.Top)
                    .asPaddingValues()
            ) {
                NoteTopBar(
                    folderName = mainScreenState.folderName,
                    selectedNumber = mainScreenState.selectNoteCount?.selectCount ?: 0,
                    spanCount = spanCount,
                    changeSpanCount = { spanCount = if (spanCount == 1) 2 else 1 },
                    showEditMode = {
                        mainScreenVM.setEvent(it)
                        if (it == MainScreenEvent.SelectAll(NoteSelectState.IDLE))
                            mainScreenVM.setEvent(MainScreenEvent.ClearSelectNotes)
                    },
                    pinItem = { mainScreenVM.setEvent(MainScreenEvent.PinNotes) },
                    deleteItem = { mainScreenVM.setEvent(MainScreenEvent.DeleteNotes) },
                    searchValue = { mainScreenVM.setEvent(MainScreenEvent.SearchNote(it)) },
                    openFolderScreen = openFolderScreen
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NotesGrid(
            spanCount = spanCount,
            mainScreenState = mainScreenState,
            openDetailNote = { uid, password ->
                if (password.isNotEmpty()) openPasswordScreen(uid, password)
                else openDetailNote(uid)
            },
            modifier = Modifier.padding(paddingValues),
            selectNote = { uid, isChecked ->
                mainScreenVM.setEvent(MainScreenEvent.SelectNote(uid, isChecked))
            }
        )
    }
}