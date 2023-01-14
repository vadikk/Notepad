package com.example.notepad.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder")
data class Folder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "uid") var uid: Int? = null,
    val title: String = "",
    val countNote: Int = 0,
    val isSelected: Boolean = false
)
