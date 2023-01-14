package com.example.notepad.domain.usecase

class ValidateNoteField {

    private val changes = mutableSetOf<DataFieldType>()

    fun validateInputFields(newText: String, oldText: String, type: DataFieldType): Boolean {
        when(type.name){
            DataFieldType.TITLE.name -> {
                if (newText != oldText) changes.add(DataFieldType.TITLE)
                else changes.remove(DataFieldType.TITLE)
            }
            DataFieldType.DESCRIPTION.name -> {
                if (newText != oldText) changes.add(DataFieldType.DESCRIPTION)
                else changes.remove(DataFieldType.DESCRIPTION)
            }
            DataFieldType.PASSWORD.name -> {
                if (newText != oldText) changes.add(DataFieldType.PASSWORD)
                else changes.remove(DataFieldType.PASSWORD)
            }
            DataFieldType.FOLDER.name -> {
                if (newText != oldText) changes.add(DataFieldType.FOLDER)
                else changes.remove(DataFieldType.FOLDER)
            }
        }
        return changes.isNotEmpty()
    }

    fun validateColor(newColor: Int, oldColor: Int, type: DataFieldType): Boolean {
        when(type.name){
            DataFieldType.COLOR.name -> {
                if (newColor != oldColor) changes.add(DataFieldType.COLOR)
                else changes.remove(DataFieldType.COLOR)
            }
        }
        return changes.isNotEmpty()
    }
}

enum class DataFieldType{
    TITLE, DESCRIPTION, COLOR, PASSWORD, FOLDER
}