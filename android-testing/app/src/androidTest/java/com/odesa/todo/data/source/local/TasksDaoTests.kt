package com.odesa.todo.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.odesa.todo.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith( AndroidJUnit4::class )
@SmallTest
class TasksDaoTests {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun cleanup() = database.close()

    @Test
    fun insertTaskAndGetById() = runTest {
        val task = Task( "title", "description" )
        database.taskDao().insertTask( task )

        val loaded = database.taskDao().getTaskById( task.id )

        assertThat( loaded as Task, notNullValue() )
        assertThat( loaded.id, `is`( task.id ) )
        assertThat( loaded.title, `is`( task.title ) )
        assertThat( loaded.description, `is`( task.description ) )
        assertThat( loaded.isCompleted, `is`( task.isCompleted ) )
    }

    @Test
    fun updateTaskAndGetById() = runTest {
        val task = Task( "Title", "Description" )
        database.taskDao().insertTask( task )
        database.taskDao().updateTask( Task( "Updated Title", "Updated Description",
            id = task.id ) )
        val loaded = database.taskDao().getTaskById( task.id ) as Task
        assertThat( loaded.id, `is`( task.id ) )
        assertThat( loaded.title, `is`( "Updated Title" ) )
        assertThat( loaded.description, `is`( "Updated Description" ) )
        assertThat( loaded.isCompleted, `is`( task.isCompleted ) )
    }
}