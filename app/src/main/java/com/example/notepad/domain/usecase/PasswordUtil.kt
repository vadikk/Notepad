package com.example.notepad.domain.usecase

enum class PasswordType(val value: Int){
    IDLE(-1), APPLY(0), CONFIRM(1), REMOVE(2)
}

fun Int.mapToPasswordType(): PasswordType {
    return when(this){
        -1 -> PasswordType.IDLE
        0 -> PasswordType.APPLY
        1 -> PasswordType.CONFIRM
        2 -> PasswordType.REMOVE
        else -> PasswordType.IDLE
    }
}