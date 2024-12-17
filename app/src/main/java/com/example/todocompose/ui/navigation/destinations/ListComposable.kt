package com.example.todocompose.ui.navigation.destinations

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.todocompose.ui.navigation.Screen
import com.example.todocompose.ui.screens.list.ListScreen
import com.example.todocompose.ui.viewModels.SharedViewModel
import com.example.todocompose.util.Action

fun NavGraphBuilder. listComposable(
    navigateToTaskScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {
    composable<Screen.List>{ navBackStackEntry ->
        val action = navBackStackEntry.toRoute<Screen.List>().action

        var myAction by rememberSaveable { mutableStateOf(Action.NO_ACTION) }

        val databaseAction = sharedViewModel.action

        LaunchedEffect(key1 = action) {
            if (action != myAction) {
                myAction = action
                sharedViewModel.updateAction(action)
            }
        }

        ListScreen(action = databaseAction, navigateToTaskScreen = navigateToTaskScreen, sharedViewModel = sharedViewModel)
    }
}