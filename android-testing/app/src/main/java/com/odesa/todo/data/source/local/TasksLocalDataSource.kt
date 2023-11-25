package com.odesa.todo.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.odesa.todo.data.Result
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource (
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksDao.observeTasks().map { Result.Success( it ) }
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext( ioDispatcher ) {
        return@withContext try {
            Result.Success( tasksDao.getTasks() )
        } catch ( e: Exception ) {
            Result.Error( e )
        }
    }

    override suspend fun refreshTasks() {
        // NO-OP
    }

    override fun observeTask( taskId: String ): LiveData<Result<Task>> {
        return tasksDao.observeTaskById( taskId ).map { Result.Success( it ) }
    }

    override suspend fun getTask( taskId: String ): Result<Task> = withContext( ioDispatcher ) {
        try {
            val task = tasksDao.getTaskById( taskId )
            if ( task != null )
                return@withContext Result.Success( task )
            else
                return@withContext Result.Error( Exception( "Task not found!" ) )
        } catch ( e: Exception ) {
            return@withContext Result.Error( e )
        }
    }

    override suspend fun refreshTask( taskId: String ) {
        // NO-OP
    }

    override suspend fun saveTask( task: Task ) {
        tasksDao.insertTask( task )
    }

    override suspend fun completeTask( task: Task ) {
        tasksDao.updateCompleted( task.id, true )
    }

    override suspend fun completeTask( taskId: String ) {
        tasksDao.updateCompleted( taskId, true )
    }

    override suspend fun activateTask( task: Task ) {
        tasksDao.updateCompleted( task.id, false )
    }

    override suspend fun activateTask( taskId: String ) {
        tasksDao.updateCompleted( taskId, false )
    }

    override suspend fun clearCompletedTasks() {
        tasksDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() {
        tasksDao.deleteTasks()
    }

    override suspend fun deleteTask( taskId: String ) = withContext<Unit>( ioDispatcher ) {
        tasksDao.deleteTaskById( taskId )
    }
}