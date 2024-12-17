package com.example.todocompose.ui.screens.list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.todocompose.R
import com.example.todocompose.ui.viewModels.SharedViewModel
import com.example.todocompose.util.Action
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    action: Action,
    navigateToTaskScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {

    LaunchedEffect(key1 = action) {
        sharedViewModel.handleDatabaseActions(action = action)
    }

    val allTasks by sharedViewModel.allTasks.collectAsState()
    val searchTasks by sharedViewModel.searchTasks.collectAsState()
    val searchAppBarState by sharedViewModel.searchAppBarState
    val searchTextState by sharedViewModel.searchTextState
    val sortState by sharedViewModel.sortState.collectAsState()
    val lowPriorityTasks by sharedViewModel.lowPriorityTasks.collectAsState()
    val highPriorityTasks by sharedViewModel.highPriorityTasks.collectAsState()


    val snackBarHostState = remember { SnackbarHostState() }

    DisplaySnackBar(
        scaffoldState = snackBarHostState,
        onCompleted = { sharedViewModel.updateAction(newAction = it) },
        undoClicked = {
            sharedViewModel.updateAction(newAction = it)
        },
        taskTitle = sharedViewModel.title,
        action = action
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = { ListAppBar(sharedViewModel, searchAppBarState, searchTextState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                ListContent(
                    allTasks = allTasks,
                    searchTasks = searchTasks,
                    lowPriorityTasks = lowPriorityTasks,
                    highPriorityTasks = highPriorityTasks,
                    sortState = sortState,
                    searchAppBarState = searchAppBarState,
                    onSwipeToDelete = { action, task ->
                        sharedViewModel.updateAction(newAction = action)
                        sharedViewModel.updateTaskFields(selectedTask = task)
                        snackBarHostState.currentSnackbarData?.dismiss()
                    },
                    navigateToTaskScreen = navigateToTaskScreen
                )
            }
        },
        floatingActionButton = {
            ListFab(onFabClicked = navigateToTaskScreen)
        })
}

@Composable
fun ListFab(
    onFabClicked: (taskId: Int) -> Unit
) {
    SmallFloatingActionButton(
        onClick = { onFabClicked(-1) }, containerColor = MaterialTheme.colorScheme.tertiary,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_button),
            tint = Color.White
        )
    }
}

@Composable
fun DisplaySnackBar(
    scaffoldState: SnackbarHostState,
    onCompleted: (Action) -> Unit,
    undoClicked: (Action) -> Unit,
    taskTitle: String,
    action: Action
) {

    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = action) {
        if (action != Action.NO_ACTION) {
            scope.launch {
                val snackBarResult = scaffoldState.showSnackbar(
                    message = setMessage(action, taskTitle),
                    actionLabel = setActionLabel(action = action),
                    duration = SnackbarDuration.Short
                )
                undoDeleteTask(
                    action = action,
                    snackBarResult = snackBarResult,
                    undoClickDelete = undoClicked
                )
            }
            onCompleted(Action.NO_ACTION)
        }
    }

}

private fun setMessage(action: Action, taskTitle: String): String {
    return when (action) {
        Action.DELETE_ALL -> "All Tasks Removed"
        else -> "${action.name}: $taskTitle"
    }
}

private fun setActionLabel(action: Action): String {
    return if (action == Action.DELETE) {
        "UNDO"
    } else {
        "OK"
    }
}

private fun undoDeleteTask(
    action: Action,
    undoClickDelete: (Action) -> Unit,
    snackBarResult: SnackbarResult
) {
    if (snackBarResult == SnackbarResult.ActionPerformed && action == Action.DELETE) {
        Log.d("Something clicked", "${SnackbarResult.ActionPerformed} and dismissed ${SnackbarResult.Dismissed}")
        undoClickDelete(Action.UNDO)
    }
}

//@Preview
//@Composable
//fun ListScreenPreview() {
//    ListScreen(navigateToTaskScreen = {})
//}