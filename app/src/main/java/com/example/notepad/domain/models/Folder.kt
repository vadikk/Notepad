package com.example.notepad.domain.models

import com.example.notepad.data.db.models.FolderEntity

data class Folder(
    var uid: Int? = null,
    val title: String = "",
    val countNote: Int = 0,
    val isSelected: Boolean = false
)

fun FolderEntity.mapToDomain(): Folder = Folder(uid, title, countNote, isSelected)

fun Folder.mapToFolderEntity(): FolderEntity = FolderEntity(uid, title, countNote, isSelected)