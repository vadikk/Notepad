package com.example.notepad.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notepad.R
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.domain.usecase.ValidationResult
import com.example.notepad.ui.theme.NotepadTheme

@Composable
fun PasswordScreen(
    passwordType: PasswordType,
    password: String,
    onBackClick: () -> Unit,
    transferPassword: (newPassword: String) -> Unit,
    openNoteDetail: () -> Unit
) {
    val passwordVM = viewModel<PasswordVM>()
    passwordVM.passOriginalPassword(password)
    val passwordState by passwordVM.passwordFlow.collectAsStateWithLifecycle(ValidationResult())

    Password(
        passwordType = passwordType,
        passwordLength = passwordVM.passwordText.length,
        validationResult = passwordState,
        onBackClick = onBackClick,
        keyboardBtnClick = { passwordVM.setPassword(it) },
        applyBtn = {
            if (passwordType == PasswordType.APPLY || passwordType == PasswordType.REMOVE) {
                val refreshPassword =
                    if (passwordType == PasswordType.APPLY) passwordVM.passwordText else ""
                transferPassword(refreshPassword)
            } else openNoteDetail()
        }
    )
}

@Composable
fun Password(
    passwordType: PasswordType,
    passwordLength: Int,
    validationResult: ValidationResult,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    keyboardBtnClick: (btn: KeyboardBtnEnum) -> Unit,
    applyBtn: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, end = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onBackClick() }
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(id = R.string.enter_passsword),
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 1..4) {
                AttemptCircle(isTypedPassword = passwordLength >= i)
                Spacer(modifier = Modifier.width(9.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(visible = validationResult.isShowError) {
            Text(
                text = stringResource(id = R.string.wrong_password),
                style = MaterialTheme.typography.h4,
                color = Color.Red,
                fontWeight = FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        KeyboardPanel(clickNumber = keyboardBtnClick)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ApplyBtn(
                passwordType = passwordType,
                isBtnVisible = validationResult.isEnableBtn,
                applyBtn = applyBtn
            )
        }
    }
}

@Composable
fun AttemptCircle(
    isTypedPassword: Boolean,
    modifier: Modifier = Modifier
) {
    val bgModifier =
        if (isTypedPassword) Modifier.background(MaterialTheme.colors.primary)
        else Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colors.onPrimary,
            shape = CircleShape
        )

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .then(bgModifier)
    )
}

@Composable
fun KeyboardBtn(
    btnValue: KeyboardBtnEnum,
    modifier: Modifier = Modifier,
    clickNumber: (btn: KeyboardBtnEnum) -> Unit
) {
    Box(
        modifier = modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(Color.LightGray.copy(alpha = 0.4F))
            .clickable { clickNumber(btnValue) },
        contentAlignment = Alignment.Center
    ) {
        if (btnValue != KeyboardBtnEnum.BTN_CLEAR) {
            Text(
                text = btnValue.value.toString(),
                style = MaterialTheme.typography.h2,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_clear),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun KeyboardPanel(
    modifier: Modifier = Modifier,
    clickNumber: (btn: KeyboardBtnEnum) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_1,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_2,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_3,
                clickNumber = clickNumber
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_4,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_5,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_6,
                clickNumber = clickNumber
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_7,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_8,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_9,
                clickNumber = clickNumber
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.size(62.dp))
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_0,
                clickNumber = clickNumber
            )
            KeyboardBtn(
                btnValue = KeyboardBtnEnum.BTN_CLEAR,
                clickNumber = clickNumber
            )
        }
    }
}

@Composable
fun ApplyBtn(
    passwordType: PasswordType,
    isBtnVisible: Boolean,
    modifier: Modifier = Modifier,
    applyBtn: () -> Unit
) {
    AnimatedVisibility(
        visible = isBtnVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(2.dp, MaterialTheme.colors.onBackground),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.LightGray.copy(alpha = 0.4F)
            ),
            contentPadding = PaddingValues(16.dp),
            onClick = { applyBtn() }
        ) {
            Text(
                text = stringResource(
                    when (passwordType) {
                        PasswordType.APPLY -> R.string.apply_password
                        PasswordType.CONFIRM -> R.string.confirm_password
                        PasswordType.REMOVE -> R.string.remove_password
                        else -> R.string.apply_password
                    }
                ),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview
@Composable
fun PasswordPreview() {
    NotepadTheme {
        Password(
            passwordType = PasswordType.IDLE,
            passwordLength = 0,
            validationResult = ValidationResult(isShowError = true, isEnableBtn = true),
            keyboardBtnClick = {},
            onBackClick = {},
            applyBtn = {}
        )
    }
}

@Preview
@Composable
fun ApplyBtnPreview() {
    NotepadTheme {
        ApplyBtn(
            passwordType = PasswordType.IDLE,
            isBtnVisible = true,
            applyBtn = {}
        )
    }
}

@Preview
@Composable
fun AttemptCirclePreview() {
    NotepadTheme {
        AttemptCircle(isTypedPassword = true)
    }
}

@Preview
@Composable
fun KeyboardBtnPreview() {
    NotepadTheme {
        KeyboardBtn(
            btnValue = KeyboardBtnEnum.BTN_CLEAR,
            clickNumber = {}
        )
    }
}

@Preview
@Composable
fun KeyboardPanelPreview() {
    NotepadTheme {
        KeyboardPanel(
            clickNumber = {}
        )
    }
}