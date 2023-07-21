package com.example.notepad.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions

@Composable
fun rememberNotepadAppState(
    navController: NavHostController = rememberNavController()
): NotepadAppState = remember(key1 = navController) {
    NotepadAppState(navController)
}

@Stable
class NotepadAppState(
    val navController: NavHostController
){
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when(currentDestination?.route) {
            selectedNoteNavRoute -> TopLevelDestination.SELECTED_NOTES
            allNotesNavRoute -> TopLevelDestination.ALL_NOTES
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    val shouldShowBottomBar: Boolean
        @Composable get() = when (currentDestination?.route) {
            selectedNoteNavRoute -> true
            allNotesNavRoute -> true
            else -> false
        }

//    val shouldAnimateFab: Boolean
//        @Composable get() = currentTopLevelDestination != destination

    @Composable
    fun shouldAnimateFab(targetDestination: TopLevelDestination): Boolean =
        currentTopLevelDestination == targetDestination

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when(topLevelDestination) {
            TopLevelDestination.SELECTED_NOTES -> navController.navigate(selectedNoteNavRoute, topLevelNavOptions)
            TopLevelDestination.ALL_NOTES -> navController.navigate(allNotesNavRoute, topLevelNavOptions)
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}