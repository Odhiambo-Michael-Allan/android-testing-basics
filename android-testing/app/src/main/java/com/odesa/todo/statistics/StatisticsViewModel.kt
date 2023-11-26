package com.odesa.todo.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.odesa.todo.TodoApplication
import com.odesa.todo.data.Result
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.DefaultTasksRepository
import com.odesa.todo.data.Result.*
import com.odesa.todo.data.source.TasksRepository
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()
    private val _dataLoading = MutableLiveData( false )
    val dataLoading: LiveData<Boolean> = _dataLoading
    private val stats: LiveData<StatsResult?> = tasks.map {
        if ( it is Success )
            getActiveAndCompletedStats( it.data )
        else
            null
    }
    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f
    }
    val completedTasksPercent: LiveData<Float> = stats.map {
        it?.completedTasksPercent ?: 0f
    }
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { ( it as? Success )?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            tasksRepository.refreshTasks()
            _dataLoading.value = false
        }
    }
}

@Suppress( "UNCHECKED_CAST" )
class StatisticsViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create( modelClass: Class<T> ) =
        ( StatisticsViewModel( tasksRepository ) as T )
}