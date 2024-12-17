package com.example.todocompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.todocompose.ui.navigation.destinations.listComposable
import com.example.todocompose.ui.navigation.destinations.taskComposable
import com.example.todocompose.ui.viewModels.SharedViewModel

@Composable
fun SetupNavigation(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List()
    ) {
        // We define our composable screens here
        listComposable(
            navigateToTaskScreen = { taskId ->
                navController.navigate(Screen.Task(id = taskId))
            },
            sharedViewModel = sharedViewModel
        )
        taskComposable(
            navigateToListScreen = { action ->
                navController.navigate(Screen.List(action = action)) {
                    popUpTo(Screen.List(action)) { inclusive = true }
                }
            },
            sharedViewModel = sharedViewModel
        )
    }
}