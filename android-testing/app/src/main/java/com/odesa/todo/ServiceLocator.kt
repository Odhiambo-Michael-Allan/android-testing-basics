package com.odesa.todo

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.odesa.todo.data.source.DefaultTasksRepository
import com.odesa.todo.data.source.TasksDataSource
import com.odesa.todo.data.source.TasksRepository
import com.odesa.todo.data.source.local.TasksLocalDataSource
import com.odesa.todo.data.source.local.ToDoDatabase
import com.odesa.todo.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set
    private val lock = Any()

    @VisibleForTesting
    fun resetRepository() {
        synchronized( lock ) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }

    fun provideTasksRepository( context: Context ): TasksRepository {
        synchronized( this ) {
            return tasksRepository ?: createTasksRepository( context )
        }
    }

    private fun createTasksRepository( context: Context ): TasksRepository {
        val newRepository = DefaultTasksRepository( TasksRemoteDataSource,
            createTaskLocalDataSource( context ) )
        tasksRepository = newRepository
        return newRepository
    }

    private fun createTaskLocalDataSource( context: Context ): TasksDataSource {
        val database = database ?: createDatabase( context )
        return TasksLocalDataSource( database.taskDao() )
    }

    private fun createDatabase( context: Context ): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }
}