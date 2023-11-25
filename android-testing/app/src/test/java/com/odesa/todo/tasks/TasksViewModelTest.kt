package com.odesa.todo.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.FakeTasksRepository
import com.odesa.todo.data.source.TasksRepository
import com.odesa.todo.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before

class TasksViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var tasksRepository: FakeTasksRepository

    @Before
    fun setup() {
        tasksRepository = FakeTasksRepository()
        val task1 = Task( "Title1", "Description1" )
        val task2 = Task( "Title2", "Description2", true )
        val task3 = Task( "Title3", "Description3", true )
        tasksRepository.addTasks( task1, task2, task3 )
        tasksViewModel = TasksViewModel( tasksRepository )
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        tasksViewModel.addNewTask()
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not( nullValue() ) )
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {
        tasksViewModel.setFiltering( TasksFilterType.ALL_TASKS )
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat( value, `is`( true ) )
    }


}