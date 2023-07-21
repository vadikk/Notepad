package com.example.notepad.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notepad.R
import com.example.notepad.ui.mainScreen.MainScreenEvent
import com.example.notepad.ui.mainScreen.NoteSelectState
import com.example.notepad.ui.theme.NotepadTheme

@Composable
fun NoteTopBar(
    folderName: String,
    selectedNumber: Int,
    spanCount: Int,
    modifier: Modifier = Modifier,
    changeSpanCount: () -> Unit,
    showEditMode: (MainScreenEvent) -> Unit,
    pinItem: () -> Unit,
    deleteItem: () -> Unit,
    searchValue: (text: String) -> Unit,
    openFolderScreen: () -> Unit
) {
    var showSearchLayout by remember { mutableStateOf(false) }
    var showEditLayout by remember { mutableStateOf(MainScreenEvent.SelectAll(NoteSelectState.IDLE)) }
    var selectAllItem by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { showEditLayout }
            .collect{
                showEditMode(it)
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showSearchLayout) {
            NoteSearch(
                searchValue = { searchValue(it) },
                hideSearch = {
                    showSearchLayout = false
                    searchValue("")
                }
            )
        } else {
            if (showEditLayout != MainScreenEvent.SelectAll(NoteSelectState.IDLE)) {
                EditLayout(
                    selectNumberValue = "$selectedNumber selected",
                    selectCount = selectedNumber,
                    isSelectAll = selectAllItem,
                    closeEditLayout = {
                        showEditLayout = MainScreenEvent.SelectAll(NoteSelectState.IDLE)
                    },
                    pinItem = {
                        pinItem()
                        showEditLayout = MainScreenEvent.SelectAll(NoteSelectState.IDLE)
                    },
                    deleteItem = {
                        deleteItem()
                        showEditLayout = MainScreenEvent.SelectAll(NoteSelectState.IDLE)
                    },
                    selectAll = {
                        selectAllItem = !selectAllItem
                        showEditLayout =
                            if (selectAllItem) MainScreenEvent.SelectAll(NoteSelectState.SELECT)
                            else MainScreenEvent.SelectAll(NoteSelectState.NOT_SELECT)
                    }
                )
            }else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = folderName,
                        style = MaterialTheme.typography.h2,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.clickable { openFolderScreen() }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow),
                        contentDescription = null
                    )
                    Box(
                        modifier = Modifier
                            .weight(1F),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.clickable { showSearchLayout = true }
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                    NoteDropDown(
                        spanCount = spanCount,
                        edit = {
                            showEditLayout = MainScreenEvent.SelectAll(NoteSelectState.NOT_SELECT)
                        },
                        changeSpanCount = changeSpanCount
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NoteTopBarPreview() {
    NotepadTheme {
        NoteTopBar(
            selectedNumber = 0,
            spanCount = 1,
            folderName = stringResource(id = R.string.all_notes),
            modifier = Modifier.background(MaterialTheme.colors.background),
            changeSpanCount = {},
            showEditMode = {},
            pinItem = {},
            searchValue = {},
            openFolderScreen = {},
            deleteItem = {}
        )
    }
}