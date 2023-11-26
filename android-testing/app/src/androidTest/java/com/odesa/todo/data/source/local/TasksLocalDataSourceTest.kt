package com.odesa.todo.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.odesa.todo.data.Result
import com.odesa.todo.data.Task
import com.odesa.todo.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat

/**
 * Integration Test.
 */
@ExperimentalCoroutinesApi
@RunWith( AndroidJUnit4::class )
@MediumTest
class TasksLocalDataSourceTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = TasksLocalDataSource(
            database.taskDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun saveTask_retrievesTask() = runTest {
        val newTask = Task( "title", "description", false )
        localDataSource.saveTask( newTask )

        val result = localDataSource.getTask( newTask.id )

        assertThat( result.succeeded, `is`( true ) )
        result as Result.Success
        assertThat( result.data.title, `is`( "title" ) )
        assertThat( result.data.description, `is`( "description" ) )
        assertThat( result.data.isCompleted, `is`( false ) )
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runTest {
        val newTask = Task( "Title", "Description" )
        localDataSource.saveTask( newTask )
        localDataSource.completeTask( newTask )
        val result = localDataSource.getTask( newTask.id )
        assertThat( result.succeeded, `is`( true ) )
        result as Result.Success
        assertThat( result.data.title, `is`( newTask.title ) )
        assertThat( result.data.description, `is`( newTask.description ) )
        assertThat( result.data.isCompleted, `is`( true ) )
    }
}