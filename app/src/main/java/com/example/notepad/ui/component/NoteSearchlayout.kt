package com.example.notepad.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notepad.ui.theme.NotepadTheme

@Composable
fun NoteSearch(
    modifier: Modifier = Modifier,
    searchValue: (text: String) -> Unit,
    hideSearch: () -> Unit
) {
    val searchFocusRequester = remember { FocusRequester() }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier.clickable { hideSearch() }
        )
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                searchValue(searchText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(searchFocusRequester),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        searchText = ""
                        searchValue(searchText)
                    }
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                leadingIconColor = MaterialTheme.colors.onBackground,
                trailingIconColor = MaterialTheme.colors.onBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Preview
@Composable
fun NoteSearchPreview() {
    NotepadTheme {
        NoteSearch(
            modifier = Modifier.background(MaterialTheme.colors.background),
            searchValue = {},
            hideSearch = {}
        )
    }
}