package com.example.todocompose.ui.navigation

import androidx.navigation.NavController
import com.example.todocompose.util.Action
import com.example.todocompose.util.Constants.LIST_SCREEN

class NavigationGraph(navController: NavController) {
//    val splash: () -> Unit = {
//        navController.navigate( "list/${Action.NO_ACTION}") {
//            popUpTo(SPLASH_SCREEN) { inclusive = true}
//        }
//    }
    val list: (Action) -> Unit = { action ->
        navController.navigate("list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }
    val task: (Int) -> Unit = { taskId ->
        navController.navigate("task/$taskId")
    }
}