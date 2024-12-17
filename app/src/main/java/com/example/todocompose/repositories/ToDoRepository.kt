package com.example.todocompose.repositories

import com.example.todocompose.data.db.ToDoDao
import com.example.todocompose.data.models.TodoTask
import javax.inject.Inject

class ToDoRepository @Inject constructor(private val todoDao: ToDoDao) {

    fun getAllTasks() = todoDao.getAllTasks()
    fun getSelectedTask(taskId: Int) = todoDao.getSelectedTask(taskId)

    suspend fun addTask(addTask: TodoTask) = todoDao.addTask(addTask)
    suspend fun updateTask(taskUpdate: TodoTask) = todoDao.updateTask(taskUpdate)

    suspend fun deleteTask(task: TodoTask) = todoDao.deleteTask(task)

    suspend fun deleteAllTask() = todoDao.deleteAllTasks()

    fun searchDatabase(searchQuery: String) = todoDao.searchDatabase(searchQuery)

    fun sortByLowPriority() = todoDao.sortByLowPriority()

    fun sortByHighPriority() = todoDao.sortByHighPriority()
}