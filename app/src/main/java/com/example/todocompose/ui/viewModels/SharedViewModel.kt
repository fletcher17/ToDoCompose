package com.example.todocompose.ui.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.models.Priority
import com.example.todocompose.data.models.TodoTask
import com.example.todocompose.repositories.DataStoreRepository
import com.example.todocompose.repositories.ToDoRepository
import com.example.todocompose.util.Action
import com.example.todocompose.util.Constants.MAX_TITLE_LENGTH
import com.example.todocompose.util.RequestState
import com.example.todocompose.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val todoRepository: ToDoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var id by mutableStateOf(0)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var priority by mutableStateOf(Priority.LOW)
        private set

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    var action by mutableStateOf(Action.NO_ACTION)
        private set

    private val _allTasks = MutableStateFlow<RequestState<List<TodoTask>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<TodoTask>>> = _allTasks

    private val _searchTasks = MutableStateFlow<RequestState<List<TodoTask>>>(RequestState.Idle)
    val searchTasks: StateFlow<RequestState<List<TodoTask>>> = _searchTasks

    private val _selectedTask: MutableStateFlow<TodoTask?> = MutableStateFlow(null)
    val selectedTask = _selectedTask

    private val _sortState = MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState = _sortState

    val lowPriorityTasks
        get() = todoRepository.sortByLowPriority().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
        )

    val highPriorityTasks : StateFlow<List<TodoTask>>
        get() = todoRepository.sortByHighPriority()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getAllTasks()
        readSortState()
    }

    private fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch {
                dataStoreRepository.readSortState.map {
                    Priority.valueOf(it)
                }.collect {
                    _sortState.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _sortState.value = RequestState.Error(e)
        }
    }


    fun persistSortState(priority: Priority) {
        viewModelScope.launch {
            dataStoreRepository.persistSortState(priority)
        }
    }

    fun searchDatabase(searchQuery: String) {
        _searchTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                todoRepository.searchDatabase("%$searchQuery%").collect {
                    _searchTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _searchTasks.value = RequestState.Error(e)
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }

    private fun getAllTasks() {
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                todoRepository.getAllTasks().collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allTasks.value = RequestState.Error(e)
        }
    }

    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            todoRepository.getSelectedTask(taskId).collect {
                Log.d("viewModel", "$it")
                _selectedTask.value = it
            }
        }
    }

    private fun addTask() {
        viewModelScope.launch {
            val todoTask = TodoTask(
                title = title,
                description = description,
                priority = priority
            )
            todoRepository.addTask(todoTask)
        }
        searchAppBarState.value = SearchAppBarState.CLOSED
    }

    private fun updateTask() {
        viewModelScope.launch {
            val todoTask = TodoTask(
                id = id,
                title = title,
                description = description,
                priority = priority
            )
            todoRepository.updateTask(todoTask)
        }
    }

    private fun deleteAllTask() {
        viewModelScope.launch {
            todoRepository.deleteAllTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            val todoTask = TodoTask(
                id = id,
                title = title,
                description = description,
                priority = priority
            )
            todoRepository.deleteTask(todoTask)
        }
    }

    fun handleDatabaseActions(action: Action) {
        when (action) {
            Action.ADD -> {
                addTask()
            }

            Action.UNDO -> {
                addTask()
            }

            Action.DELETE -> {
                deleteTask()
            }

            Action.DELETE_ALL -> {
                deleteAllTask()
            }

            Action.UPDATE -> {
                updateTask()
            }

            else -> {

            }
        }
    }

    fun updateTaskFields(selectedTask: TodoTask?) {
        if (selectedTask != null) {
            id = selectedTask.id
            title = selectedTask.title
            description = selectedTask.description
            priority = selectedTask.priority
        } else {
            id = 0
            title = ""
            description = ""
            priority = Priority.LOW
        }
    }

    fun updateTitle(titleText: String) {
        if (titleText.length <= MAX_TITLE_LENGTH) {
            title = titleText
        }
    }

    fun updatePriority(newPriority: Priority) {
        priority = newPriority
    }

    fun updateDescription(newDescription : String) {
        description = newDescription
    }

    fun updateAction(newAction: Action) {
        action = newAction
    }

    fun validateFields(): Boolean {
        return title.isNotEmpty() && description.isNotEmpty()
    }
}