package com.example.notepad.domain.usecase


class ValidatePassword {

    fun execute(checkedPassword: String, originalPassword: String): ValidationResult{
        if (checkedPassword.length < 4){
            return ValidationResult(isShowError = false, isEnableBtn = false)
        }
        if (originalPassword.isNotEmpty() && checkedPassword != originalPassword)
            return ValidationResult(isShowError = true, isEnableBtn = false)

        return ValidationResult(isShowError = false, isEnableBtn = true)
    }
}