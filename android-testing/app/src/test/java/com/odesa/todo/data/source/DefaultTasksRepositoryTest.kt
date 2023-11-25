package com.odesa.todo.data.source

import com.odesa.todo.data.Result
import com.odesa.todo.data.Task
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.odesa.todo.data.Result.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsEqual
import org.hamcrest.MatcherAssert.assertThat

class DefaultTasksRepositoryTest {
    private val task1 = Task( "Title1", "Description1" )
    private val task2 = Task( "Title2", "Description2" )
    private val task3 = Task( "Title3", "Description3" )
    private val remoteTasks = listOf( task1, task2 ).sortedBy { it.id }
    private val localTasks = listOf( task3 )
    private val newTasks = listOf( task3 )

    private lateinit var tasksRemoteDataSource: TasksDataSource
    private lateinit var tasksLocalDataSource: TasksDataSource

    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun setup() {
        tasksRemoteDataSource = FakeDataSource( remoteTasks.toMutableList() )
        tasksLocalDataSource = FakeDataSource( localTasks.toMutableList() )
        tasksRepository = DefaultTasksRepository(
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
        )
    }

    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = runTest {
        val tasks = tasksRepository.getTasks( true ) as Success
        assertThat( tasks.data, IsEqual( remoteTasks ) )
    }
}