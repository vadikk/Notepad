package com.example.notepad.ui.navigation

import com.example.notepad.R

enum class TopLevelDestination(
    val selectedIcon: Int,
    val titleTextId: Int,
) {
    SELECTED_NOTES(
        selectedIcon = R.drawable.ic_select_notes,
        titleTextId = R.string.select_notes
    ),
    ALL_NOTES(
        selectedIcon = R.drawable.ic_notes,
        titleTextId = R.string.all_notes
    )
}