package com.example.todocompose.ui.navigation.destinations

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.todocompose.ui.navigation.Screen
import com.example.todocompose.ui.screens.task.TaskScreen
import com.example.todocompose.ui.viewModels.SharedViewModel
import com.example.todocompose.util.Action

fun NavGraphBuilder.taskComposable(
    navigateToListScreen: (Action) -> Unit,
    sharedViewModel: SharedViewModel
) {
    composable<Screen.Task>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = {
                    -it
                },
                animationSpec = tween(
                    durationMillis = 600
                )
            )
        }
    ) { navBackStackEntry ->
        val taskId = navBackStackEntry.toRoute<Screen.Task>().id
        LaunchedEffect(key1 = taskId) {
            sharedViewModel.getSelectedTask(taskId = taskId)
        }
        val selectedTask by sharedViewModel.selectedTask.collectAsState()

        LaunchedEffect(key1 = selectedTask) {
            if (selectedTask != null || taskId == -1) {
                sharedViewModel.updateTaskFields(selectedTask)
            }
        }

        TaskScreen(
            selectedTask = selectedTask,
            sharedViewModel = sharedViewModel,
            navigateToListScreen = navigateToListScreen
        )
    }
}