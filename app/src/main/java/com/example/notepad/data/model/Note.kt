package com.example.notepad.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notepad.R

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "uid") var uid: Int? = null,
    val date: String = "",
    val folder: String = "",
    val title: String = "",
    val description: String = "",
    val isPinned: Boolean = false,
    val bgColor: Int = R.color.white,
    val password: String = ""
)
