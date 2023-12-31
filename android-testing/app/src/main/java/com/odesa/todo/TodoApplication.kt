package com.odesa.todo

import android.app.Application
import com.odesa.todo.data.source.TasksRepository
import timber.log.Timber

/**
 * An application that lazily provides a repository. Not that this Service Locator pattern is used
 * to simplify the sample. Consider a Dependency Injection framework.
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production
 * setups.
 */
class TodoApplication : Application() {

    val tasksRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository( this )

    override fun onCreate() {
        super.onCreate()
        if ( BuildConfig.DEBUG ) Timber.plant( Timber.DebugTree() )
    }
}