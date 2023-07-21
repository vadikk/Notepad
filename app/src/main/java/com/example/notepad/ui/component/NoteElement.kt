package com.example.notepad.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notepad.R
import com.example.notepad.data.utils.showCurrentDate
import com.example.notepad.ui.mainScreen.MainScreenState
import com.example.notepad.ui.mainScreen.NoteSelectState
import com.example.notepad.ui.theme.NotepadTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesGrid(
    spanCount: Int,
    mainScreenState: MainScreenState,
    modifier: Modifier = Modifier,
    openDetailNote: (uid: Int, password: String) -> Unit,
    selectNote: (uid: Int?, isChecked: Boolean) -> Unit
) {
    val noteStates = mainScreenState.modifyNotes

    Surface(
        color = MaterialTheme.colors.primary.copy(0.2F),
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(spanCount),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp
        ) {
            items(
                items = noteStates,
                key = { it.uid ?: 0 }
            ) {noteState ->
                NoteItem(
                    title = noteState.noteEntity.title,
                    description = noteState.noteEntity.description,
                    password = noteState.noteEntity.password,
                    isPinnedNote = noteState.noteEntity.isPinned,
                    date = showCurrentDate(noteState.noteEntity.date.toLong()),
                    isLockedNote = noteState.noteEntity.password.isNotEmpty(),
                    isSelectedMode = noteState.selectState != NoteSelectState.IDLE,
                    isSelected = noteState.selectState == NoteSelectState.SELECT,
                    openDetailNote = { openDetailNote(noteState.noteEntity.uid ?: 0, noteState.noteEntity.password) },
                    selectNote = { selectNote(noteState.uid, it) },
                    colorBG = noteState.noteEntity.bgColor
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    title: String,
    description: String,
    password: String,
    date: String,
    colorBG: Int,
    isPinnedNote: Boolean,
    isLockedNote: Boolean,
    isSelectedMode: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    openDetailNote: () -> Unit,
    selectNote: (isSelect: Boolean) -> Unit
){
    val blurModifier =
        if (description.isNotEmpty() && password.isNotEmpty()) Modifier.blur(8.dp, BlurredEdgeTreatment.Unbounded)
        else Modifier

    Row(
        modifier = modifier
            .background(
                color = colorResource(id = colorBG),
                shape = RoundedCornerShape(15.dp)
            )
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(14.dp)
            .clickable { openDetailNote() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .padding(end = if (isSelectedMode) 20.dp else 0.dp)
        ) {
            if(title.isNotEmpty()) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if(description.isNotEmpty()) {
                Text(
                    text = description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(blurModifier)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                if(isPinnedNote) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pinned_active),
                        contentDescription = null,
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(
                    text = date,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                if(isLockedNote) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
        if(isSelectedMode) {
            NoteRadioBtn(
                isSelected = isSelected,
                changeSelectState = { selectNote(it.not()) }
            )
        }
    }
}

@Composable
private fun NoteRadioBtn(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    changeSelectState: (isSelected: Boolean) -> Unit
) {

    val selectorIcon = painterResource(
        id = if (isSelected) R.drawable.ic_apply_select
        else R.drawable.ic_select_empty
    )

    Row(
        modifier = modifier
            .height(24.dp)
            .selectable(
                selected = isSelected,
                onClick = { changeSelectState(isSelected) },
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = selectorIcon,
            contentDescription = null,
            tint = Color.Black
        )
    }
}

@Preview
@Composable
fun NoteRadioBtnPreview() {
    NotepadTheme {
        NoteRadioBtn(
            isSelected = true,
            changeSelectState = {}
        )
    }
}

@Preview
@Composable
fun NoteItemPreview() {
    NotepadTheme {
        NoteItem(
            title = "Go shopping",
            description = "Need to buy milk 2L, eggs - 25, sugar - 1kg, spaghetti, " +
                    "pasta, ice-cream, tomato, ",
            password = "",
            isPinnedNote = true,
            date = "1/2/2023 23:07 | sdfs",
            colorBG = 0,
            isLockedNote = true,
            isSelectedMode = true,
            isSelected = false,
            openDetailNote = {},
            selectNote = {}
        )
    }
}
