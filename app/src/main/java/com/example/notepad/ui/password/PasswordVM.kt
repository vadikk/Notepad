package com.example.notepad.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.domain.usecase.ValidatePassword
import com.example.notepad.domain.usecase.ValidationResult
import com.example.notepad.ui.view.KeyboardBtnEnum
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PasswordVM(
    private val validatePassword: ValidatePassword = ValidatePassword()
): ViewModel() {

    private val _passwordFlow = MutableSharedFlow<ValidationResult>()
    val passwordFlow = _passwordFlow.asSharedFlow()

    private var originalPassword = ""
    var passwordText = ""
        private set

    fun passOriginalPassword(password: String) {
        originalPassword = password
    }

    fun setPassword(keyboardBtnEnum: KeyboardBtnEnum) {
        if (passwordText.length > 4) return

        if (keyboardBtnEnum == KeyboardBtnEnum.BTN_CLEAR){
            passwordText =
                if (passwordText.isNotEmpty()) passwordText.substring(0, passwordText.length - 1)
                else ""
            checkBtnState()

        }else {
            if (passwordText.length < 4){
                passwordText += keyboardBtnEnum.value
                checkBtnState()
            }
        }


    }

    private fun checkBtnState() {
        viewModelScope.launch {
            _passwordFlow.emit(
                validatePassword.execute(passwordText, originalPassword)
            )
        }
    }
}