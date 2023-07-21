package com.example.notepad.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notepad.R
import com.example.notepad.ui.theme.NotepadTheme

@Composable
fun EditLayout(
    selectNumberValue: String,
    selectCount: Int,
    isSelectAll: Boolean,
    modifier: Modifier = Modifier,
    closeEditLayout: () -> Unit,
    pinItem: () -> Unit,
    deleteItem: () -> Unit,
    selectAll: () -> Unit
) {
    val colorState: Color by animateColorAsState(
        if (selectCount > 0) MaterialTheme.colors.onBackground else MaterialTheme.colors.primary
    )
    val selectDrawable: Int by animateIntAsState(
        if (isSelectAll) R.drawable.ic_apply_select else R.drawable.ic_select_empty
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = null,
            modifier = Modifier.clickable { closeEditLayout() }
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = selectNumberValue,
            modifier = Modifier
                .weight(1F)
        )
        Spacer(modifier = Modifier.width(30.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_pinned_inactive),
            contentDescription = null,
            tint = colorState,
            modifier = Modifier
                .size(20.dp)
                .clickable { if (selectCount > 0) pinItem() }
        )
        Spacer(modifier = Modifier.width(30.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_trash_inactive),
            contentDescription = null,
            tint = colorState,
            modifier = Modifier
                .size(20.dp)
                .clickable { if (selectCount > 0) deleteItem() }
        )
        Spacer(modifier = Modifier.width(30.dp))
        Icon(
            painter = painterResource(id = selectDrawable),
            contentDescription = null,
            modifier = Modifier.clickable { selectAll() }
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Preview
@Composable
fun EditLayoutPreview() {
    NotepadTheme {
        EditLayout(
            selectNumberValue = "1 item",
            selectCount = 0,
            isSelectAll = false,
            modifier = Modifier.background(MaterialTheme.colors.background),
            closeEditLayout = {},
            pinItem = {},
            deleteItem = {},
            selectAll = {}
        )
    }
}