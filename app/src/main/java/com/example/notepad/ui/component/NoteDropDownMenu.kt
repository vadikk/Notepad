package com.example.notepad.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notepad.R

@Composable
fun NoteDropDown(
    spanCount: Int,
    edit: () -> Unit,
    changeSpanCount: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val spanCountText =
        if (spanCount == 1) stringResource(id = R.string.grid_view)
        else stringResource(id = R.string.list_view)

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
            .padding(end = 12.dp)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            DropdownMenuItem(
                onClick = {
                    edit()
                    expanded = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.edit),
                    color = MaterialTheme.colors.onBackground
                )
            }
            DropdownMenuItem(
                onClick = {
                    changeSpanCount()
                    expanded = false
                }
            ) {
                Text(
                    text = spanCountText,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}