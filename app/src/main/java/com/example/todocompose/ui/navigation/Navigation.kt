package com.example.todocompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.todocompose.ui.navigation.destinations.listComposable
import com.example.todocompose.ui.navigation.destinations.splashComposable
import com.example.todocompose.ui.navigation.destinations.taskComposable
import com.example.todocompose.ui.viewModels.SharedViewModel
import com.example.todocompose.util.Constants.LIST_SCREEN
import com.example.todocompose.util.Constants.SPLASH_SCREEN

@Composable
fun SetupNavigation(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val screen = remember(navController) {
        NavigationGraph(navController = navController)
    }

    NavHost(
        navController = navController,
        startDestination = LIST_SCREEN
    ) {
        // We define our composable screens here
//        splashComposable(navigateToListScreen = screen.splash)
        listComposable(
            navigateToTaskScreen = screen.task,
            sharedViewModel = sharedViewModel
        )
        taskComposable(
            navigateToListScreen = screen.list,
            sharedViewModel = sharedViewModel
        )
    }
}