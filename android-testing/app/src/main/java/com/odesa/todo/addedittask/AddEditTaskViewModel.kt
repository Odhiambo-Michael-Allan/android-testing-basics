package com.odesa.todo.addedittask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.*
import com.odesa.todo.Event
import com.odesa.todo.R
import com.odesa.todo.TodoApplication
import com.odesa.todo.data.Result
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.DefaultTasksRepository
import kotlinx.coroutines.launch

class AddEditTaskViewModel( application: Application ) : AndroidViewModel( application ) {

    private val tasksRepository = ( application as TodoApplication ).tasksRepository

    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private var taskId: String? = null
    private var isNewTask: Boolean = false
    private var isDataLoaded = false
    private var taskCompleted = false

    fun start( taskId: String? ) {
        if ( _dataLoading.value == true )
            return
        this.taskId = taskId
        if ( taskId == null ) {
            // No need to populate, it's a new task
            isNewTask = true
            return
        }
        if ( isDataLoaded ) {
            // No need to populate, already have data
            return
        }
        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            tasksRepository.getTask( taskId ).let {
                if ( it is Result.Success ) onTaskLoaded( it.data ) else onDataNotAvailable()
            }
        }
    }

    private fun onTaskLoaded( task: Task ) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab
    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        if ( currentTitle == null || currentDescription == null ) {
            _snackbarText.value = Event( R.string.empty_task_message )
            return
        }
        if ( Task( currentTitle, currentDescription ).isEmpty ) {
            _snackbarText.value = Event( R.string.empty_task_message )
            return
        }
        val currentTaskId = taskId
        if ( isNewTask || currentTaskId == null )
            createTask( Task( currentTitle, currentDescription ) )
        else {
            val task = Task( currentTitle, currentDescription, taskCompleted, currentTaskId )
            updateTask( task )
        }
    }

    private fun createTask( newTask: Task ) = viewModelScope.launch {
        tasksRepository.saveTask( newTask )
        _taskUpdatedEvent.value = Event( Unit )
    }

    private fun updateTask( task: Task ) {
        if ( isNewTask ) throw RuntimeException( "updateTask() was but task is new" )
        viewModelScope.launch {
            tasksRepository.saveTask( task )
            _taskUpdatedEvent.value = Event( Unit )
        }
    }
}