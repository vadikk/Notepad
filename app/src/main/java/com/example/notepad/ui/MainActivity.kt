package com.example.notepad.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.notepad.R
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.ui.folderList.FoldersBottomSheet
import com.example.notepad.ui.createFolder.CreateFolderNameVM
import com.example.notepad.ui.folderList.FolderListVM
import com.example.notepad.ui.folderList.FoldersEvent
import com.example.notepad.ui.mainScreen.MainScreenVM
import com.example.notepad.ui.navigation.NoteBottomNavBar
import com.example.notepad.ui.navigation.NoteNavHost
import com.example.notepad.ui.navigation.NotepadAppState
import com.example.notepad.ui.navigation.allNotesNavRoute
import com.example.notepad.ui.navigation.createEditNoteNavRoute
import com.example.notepad.ui.navigation.rememberNotepadAppState
import com.example.notepad.ui.navigation.selectedNoteNavRoute
import com.example.notepad.ui.theme.NotepadTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun mainVMFactory(): MainScreenVM.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.Transparent.value.toInt()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            NotepadTheme {
                NotepadApp()
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class,
        ExperimentalLayoutApi::class
    )
    @Composable
    fun NotepadApp(
        notepadState: NotepadAppState = rememberNotepadAppState(
            navController = rememberNavController()
        )
    ) {
        val createFolderNameVM: CreateFolderNameVM = hiltViewModel()
        val folderListVM: FolderListVM = hiltViewModel()
        val folderListState by folderListVM.viewState.collectAsStateWithLifecycle()

        val scope = rememberCoroutineScope()

        var shouldAnimateFab by remember { mutableStateOf(true) }
        val foldersBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        val createFolderBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

        BackHandler(foldersBottomSheetState.isVisible) {
            scope.launch { foldersBottomSheetState.hide() }
        }
        BackHandler(createFolderBottomSheetState.isVisible) {
            scope.launch { createFolderBottomSheetState.hide() }
        }
        
        LaunchedEffect(key1 = Unit){
            collectWithLifecycleState(createFolderNameVM.effect) {
                scope.launch { createFolderBottomSheetState.hide() }
            }
            collectWithLifecycleState(folderListVM.effect) {
                scope.launch { foldersBottomSheetState.hide() }
            }

            notepadState.navController.currentBackStackEntryFlow
                .collect{
                    when(it.destination.route.orEmpty()) {
                        selectedNoteNavRoute, allNotesNavRoute -> {
                            if (shouldAnimateFab) {
                                shouldAnimateFab = false
                                delay(200)
                                shouldAnimateFab = true
                            } else shouldAnimateFab = true
                        }
                        else -> shouldAnimateFab = false
                    }
                }
        }

        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    visible = shouldAnimateFab,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                           notepadState.navController.navigate("$createEditNoteNavRoute/${-1}")
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            },
            bottomBar = {
                if (notepadState.shouldShowBottomBar) {
                    NoteBottomNavBar(
                        destinations = notepadState.topLevelDestinations,
                        onNavigateToDestination = { notepadState.navigateToTopLevelDestination(it) },
                        currentDestination = notepadState.currentTopLevelDestination
                    )
                }
            },
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
            )
        ) { paddingValues ->
            NoteNavHost(
                navController = notepadState.navController,
                    modifier = Modifier.padding(paddingValues),
                openFolderScreen = { scope.launch { foldersBottomSheetState.show() } }
            )
        }

        FoldersBottomSheet(
            folderList = folderListState.folders,
            sheetState = foldersBottomSheetState,
            hideBottomSheet = { scope.launch { foldersBottomSheetState.hide() } },
            createNewFolder = { scope.launch { createFolderBottomSheetState.show() } },
            selectFolder = { folderListVM.setEvent(FoldersEvent.SelectFolder(it)) }
        )

        CreateFolderBottomSheet(
            sheetState = createFolderBottomSheetState,
            hideBottomSheet = { scope.launch { createFolderBottomSheetState.hide() } },
            applyNewFolder = { name -> createFolderNameVM.createFolder(name) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateFolderBottomSheet(
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    hideBottomSheet: () -> Unit,
    applyNewFolder: (name: String) -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background,
        scrimColor = MaterialTheme.colors.primary.copy(alpha = 0.7F),
        sheetContent = {
            CreateFolderContent(
                hideBottomSheet = hideBottomSheet,
                applyNewFolder = applyNewFolder
            )
        }
    ) {}
}

@Composable
fun CreateFolderContent(
    hideBottomSheet: () -> Unit,
    applyNewFolder: (name: String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colors.background),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                modifier = Modifier.clickable { hideBottomSheet() }
            )
            Text(
                text = stringResource(id = R.string.new_folder),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1F)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_apply),
                contentDescription = null,
                modifier = Modifier.clickable {
                    applyNewFolder(searchText)
                    focusManager.clearFocus()
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Normal
            ),
            decorationBox = { innerTextField ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp)
//                        .drawWithContent {
//                            drawContent()
//                            drawLine(
//                                color = Color.LightGray,
//                                strokeWidth = 1.dp.toPx(),
//                                start = Offset(0F, size.height),
//                                end = Offset(size.width, size.height)
//                            )
//                        }
                        .drawBehind {
                            val strokeWidth = -2.dp.toPx() * density
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                color = Color.LightGray,
                                strokeWidth = 1.dp.toPx(),
                                start = Offset(0F, y),
                                end = Offset(size.width, y)
                            )
                        }
                ) {
                    Row {
                        if (searchText.isEmpty()){
                            Text(
                                text = stringResource(id = R.string.folder_name),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.LightGray
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview
@Composable
fun CreateFolderContentPreview() {
    NotepadTheme {
        CreateFolderContent(
            hideBottomSheet = {},
            applyNewFolder = {}
        )
    }
}