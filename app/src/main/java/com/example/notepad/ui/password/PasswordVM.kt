package com.example.notepad.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.domain.usecase.ValidatePassword
import com.example.notepad.domain.usecase.ValidationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PasswordVM(
    private val validatePassword: ValidatePassword = ValidatePassword()
): ViewModel() {

    private val _passwordFlow = MutableSharedFlow<ValidationResult>()
    val passwordFlow = _passwordFlow.asSharedFlow()

    private var originalPassword = ""
    var passwordText by mutableStateOf("")
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

enum class KeyboardBtnEnum(val value: Int){
    BTN_0(0),
    BTN_1(1),
    BTN_2(2),
    BTN_3(3),
    BTN_4(4),
    BTN_5(5),
    BTN_6(6),
    BTN_7(7),
    BTN_8(8),
    BTN_9(9),
    BTN_CLEAR(-1)
}