package com.example.notepad.ui.createNote

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notepad.R
import com.example.notepad.data.utils.showCurrentDate
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.domain.usecase.checkPasswordType
import com.example.notepad.ui.colorPicker.ColorItem
import com.example.notepad.ui.colorPicker.ColorPickerVM
import com.example.notepad.ui.colorPicker.ColorPickerVMFactory
import com.example.notepad.ui.folderList.FoldersBottomSheet
import com.example.notepad.ui.folderList.FolderChooseVM
import com.example.notepad.ui.folderList.FolderEntityContract
import com.example.notepad.ui.theme.NotepadTheme
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateNoteScreen(
    refreshPassword: String,
    passwordType: PasswordType,
    onBackClick: () -> Unit,
    openPasswordScreen: (password: String, passwordType: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val foldersBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    var showColorPickerDialog by remember { mutableStateOf(false) }

    val createNoteVM = hiltViewModel<CreateNoteVM>()
    val createNoteState by createNoteVM.viewState.collectAsStateWithLifecycle()

    val folderListVM: FolderChooseVM = hiltViewModel()
    val folderListState by folderListVM.viewState.collectAsStateWithLifecycle()

    val colorPickerVM: ColorPickerVM =
        viewModel(factory = ColorPickerVMFactory(createNoteVM.currentState.color))
    val colorPickerState by colorPickerVM.colorFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    BackHandler(foldersBottomSheetState.isVisible) { scope.launch { foldersBottomSheetState.hide() } }

    LaunchedEffect(key1 = Unit) {
        createNoteVM.effect.collect {
            when (it) {
                is NoteEffect.CloseScreen -> onBackClick()
            }
        }
    }

    if (passwordType == PasswordType.REMOVE || passwordType == PasswordType.APPLY) {
        createNoteVM.setEvent(NoteEvent.ChangePassword(refreshPassword))
    }

    CreateNote(
        createNoteState = createNoteState,
        passwordType = checkPasswordType(createNoteVM.currentNote),
        exit = { onBackClick() },
        applyChanges = { createNoteVM.setEvent(NoteEvent.Save) },
        share = {
            context.shareNote(
                createNoteVM.currentState.title.trim(),
                createNoteVM.currentState.description.trim()
            )
        },
        sendEvent = { createNoteVM.setEvent(it) },
        callbackMenu = {
            when (it) {
                is AddToFolder -> {
                    scope.launch { foldersBottomSheetState.show() }
                }
                is ChangeBgColor -> showColorPickerDialog = true
                is SetDeletePassword -> openPasswordScreen(
                    createNoteVM.currentNote?.password.orEmpty(),
                    checkPasswordType(createNoteVM.currentNote).value
                )
                is Delete -> createNoteVM.setEvent(NoteEvent.Delete)
                else -> Unit
            }
        }
    )

    FoldersBottomSheet(
        folderList = folderListState.folders,
        sheetState = foldersBottomSheetState,
        hideBottomSheet = { scope.launch { foldersBottomSheetState.hide() } },
        createNewFolder = { },
        selectFolder = {
            createNoteVM.setEvent(NoteEvent.Folder(it.toString()))
            scope.launch { foldersBottomSheetState.hide() }
        }
    )

    if (showColorPickerDialog) {
        colorPickerVM.changeSelectColor(createNoteVM.currentState.color)

        ChangeBGColor(
            colors = colorPickerState,
            onDismissRequest = { showColorPickerDialog = false },
            selectColor = { colorID ->
                createNoteVM.setEvent(NoteEvent.ChangeColor(colorID))
                showColorPickerDialog = false
            }
        )
    }
}

@Composable
fun CreateNote(
    createNoteState: CreateNoteState,
    passwordType: PasswordType,
    modifier: Modifier = Modifier,
    exit: () -> Unit,
    applyChanges: () -> Unit,
    sendEvent: (noteEvent: NoteEvent) -> Unit,
    share: () -> Unit,
    callbackMenu: (noteMenuAction: NoteMenuAction) -> Unit
) {
    val time by remember(createNoteState.date) {
        mutableStateOf(
            if (createNoteState.date.isNotEmpty()) showCurrentDate(createNoteState.date.toLong())
            else showCurrentDate(Calendar.getInstance().timeInMillis)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = createNoteState.color))
            .padding(14.dp)
            .systemBarsPadding()
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { exit() }
            )
            Spacer(modifier = Modifier.weight(1F))
            AnimatedVisibility(visible = createNoteState.isApplyBtnEnabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_apply),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { applyChanges() }
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_share),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { share() }
            )
            Spacer(modifier = Modifier.width(14.dp))
            CreateNoteMenu(
                passwordType = passwordType,
                callbackMenu = callbackMenu
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                text = time,
                modifier = Modifier.weight(1F),
                style = MaterialTheme.typography.body2.copy(
                    color = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            if (createNoteState.isPinned) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pinned_active),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        BasicTextField(
            value = createNoteState.title,
            onValueChange = { sendEvent(NoteEvent.ChangeTitle(it)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            textStyle = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Normal
            ),
            decorationBox = { innerTextField ->
                Row {
                    if (createNoteState.title.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.note_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        BasicTextField(
            value = createNoteState.description,
            onValueChange = { sendEvent(NoteEvent.ChangeDescription(it)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Normal
            ),
            decorationBox = { innerTextField ->
                Row {
                    if (createNoteState.description.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.note_description),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun CreateNoteMenu(
    passwordType: PasswordType,
    callbackMenu: (noteMenuAction: NoteMenuAction) -> Unit
) {
    val menuActions = remember { noteMenuActions }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            menuActions.forEach { action ->
                DropdownMenuItem(
                    onClick = {
                        callbackMenu(action)
                        expanded = false
                    }
                ) {
                    Text(
                        text = stringResource(
                            if (action !is SetDeletePassword) action.title
                            else {
                                if (passwordType == PasswordType.REMOVE) R.string.remove_password
                                else R.string.set_password
                            }
                        ),
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun ChangeBGColor(
    colors: List<ColorItem>,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    selectColor: (color: Int) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .clip(RectangleShape)
                .background(Color.LightGray)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(0.65F)
            ) {
                items(colors) {
                    BgColorItem(
                        colorItem = it,
                        selectColor = { selectColor(it.colorID) }
                    )
                }
            }
        }
    }
}

@Composable
fun BgColorItem(
    colorItem: ColorItem,
    modifier: Modifier = Modifier,
    selectColor: () -> Unit
) {
    val borderModifier =
        if (colorItem.isChecked) Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colors.onBackground,
            shape = CircleShape
        ) else Modifier

    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(colorResource(id = colorItem.colorID))
            .then(borderModifier)
            .clickable { selectColor() }
    )
}

@Preview
@Composable
fun ColorItemPreview() {
    NotepadTheme {
        BgColorItem(
            colorItem = ColorItem(),
            selectColor = {}
        )
    }
}

@Preview
@Composable
fun ChangeBGColorPreview() {
    NotepadTheme {
        ChangeBGColor(
            colors = emptyList(),
            onDismissRequest = {},
            selectColor = {}
        )
    }
}

@Preview
@Composable
fun CreateNoteToolbarPreview() {
    NotepadTheme {
        CreateNote(
            createNoteState = CreateNoteState(),
            passwordType = PasswordType.IDLE,
            modifier = Modifier.background(MaterialTheme.colors.background),
            exit = {},
            applyChanges = {},
            share = {},
            sendEvent = {},
            callbackMenu = {}
        )
    }
}

private fun Context.shareNote(title: String, description: String) {
    val finalText =
        if (title.isNotEmpty() && description.isNotEmpty()) title + "\n" + description
        else title + description

    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, finalText)
        type = "text/plain"
    }
    startActivity(Intent.createChooser(intent, "Share Note"))
}