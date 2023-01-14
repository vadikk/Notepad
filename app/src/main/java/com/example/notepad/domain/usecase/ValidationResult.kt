package com.example.notepad.domain.usecase

data class ValidationResult(
    val isShowError: Boolean = false,
    val isEnableBtn: Boolean = false
)
