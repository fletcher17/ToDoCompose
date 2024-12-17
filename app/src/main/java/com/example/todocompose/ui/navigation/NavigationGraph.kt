package com.example.todocompose.ui.navigation

import com.example.todocompose.util.Action
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data class List(val action: Action = Action.NO_ACTION): Screen()
    @Serializable
    data class Task(val id: Int) : Screen()
}