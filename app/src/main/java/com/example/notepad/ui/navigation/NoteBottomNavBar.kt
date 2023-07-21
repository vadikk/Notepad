package com.example.notepad.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.domain.usecase.mapToPasswordType
import com.example.notepad.ui.AllNotesScreen
import com.example.notepad.ui.createNote.CreateNoteScreen
import com.example.notepad.ui.mainScreen.SelectNoteScreen
import com.example.notepad.ui.password.PasswordScreen

const val selectedNoteNavRoute = "select_note_route"
const val allNotesNavRoute = "all_note_route"
const val createEditNoteNavRoute = "create_edit_note_route"
const val passwordNavRoute = "password_route"
const val noteUid = "noteUid"
const val password = "password"
const val passwordType = "passwordType"

@Composable
fun NoteBottomNavBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: TopLevelDestination?,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.Yellow,
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination,
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = destination.selectedIcon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text(stringResource(id = destination.titleTextId)) },
                onClick = { onNavigateToDestination(destination) },
                selectedContentColor = MaterialTheme.colors.onBackground,
                unselectedContentColor = MaterialTheme.colors.background
            )
        }
    }
}

@Composable
fun NoteNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = selectedNoteNavRoute,
    openFolderScreen: () -> Unit
) {
    var noteUidExtra by remember { mutableStateOf(-1) }
    var passTypeNoteEdit by remember { mutableStateOf(PasswordType.IDLE) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = selectedNoteNavRoute) {
            passTypeNoteEdit = PasswordType.IDLE

            SelectNoteScreen(
                openDetailNote = { movieID ->
                    noteUidExtra = movieID
                    navController.navigate(
                        "$createEditNoteNavRoute/$movieID"
                    )
                },
                openPasswordScreen = { uid, pass ->
                    noteUidExtra = uid
                    navController.navigate(
                        "$passwordNavRoute?$passwordType=${PasswordType.CONFIRM.value},$password=$pass"
                    )
                },
                modifier = modifier,
                openFolderScreen = openFolderScreen
            )
        }
        composable(route = allNotesNavRoute) {
            passTypeNoteEdit = PasswordType.IDLE

            AllNotesScreen(
                openDetailNote = { movieID ->
                    noteUidExtra = movieID
                    navController.navigate(
                        "$createEditNoteNavRoute/$movieID"
                    )
                },
                openPasswordScreen = { uid, pass ->
                    noteUidExtra = uid
                    navController.navigate(
                        "$passwordNavRoute?$passwordType=${PasswordType.CONFIRM.value},$password=$pass"
                    )
                },
                modifier = modifier
            )
        }
        composable(
            route = "$createEditNoteNavRoute/{$noteUid}",
            arguments = listOf(
                navArgument(noteUid) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val refreshPassword by backStackEntry.savedStateHandle.getStateFlow(password, "")
                .collectAsStateWithLifecycle()

            CreateNoteScreen(
                refreshPassword = refreshPassword,
                passwordType = passTypeNoteEdit,
                onBackClick = { navController.popBackStack() },
                openPasswordScreen = { pass, passType ->
                    passTypeNoteEdit = passType.mapToPasswordType()
                    navController.navigate(
                        "$passwordNavRoute?$passwordType=$passType,$password=$pass"
                    )
                }
            )
        }
        composable(
            route = "$passwordNavRoute?$passwordType={$passwordType},$password={$password}",
            arguments = listOf(
                navArgument(passwordType) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(password) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val passwordValue = backStackEntry.arguments?.getString(password).orEmpty()
            val passType = backStackEntry.arguments?.getInt(passwordType) ?: -1

            PasswordScreen(
                passwordType = passType.mapToPasswordType(),
                password = passwordValue,
                onBackClick = { navController.popBackStack() },
                transferPassword = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(password, it)

                    navController.popBackStack()
                },
                openNoteDetail = {
                    navController.popBackStack()
                    navController.navigate(
                        "$createEditNoteNavRoute/$noteUidExtra"
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
