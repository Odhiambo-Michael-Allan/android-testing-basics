package com.odesa.todo.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.odesa.todo.R
import com.odesa.todo.ServiceLocator
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.FakeAndroidTasksRepository
import com.odesa.todo.data.source.TasksRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Context
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith( AndroidJUnit4::class )
@MediumTest
class TasksFragmentTest {

    private lateinit var tasksRepository: TasksRepository

    @Before
    fun setup() {
        tasksRepository = FakeAndroidTasksRepository()
        ServiceLocator.tasksRepository = tasksRepository
    }

    @After
    fun cleanup() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_navigateToDetailFragmentOne() = runTest {
        tasksRepository.saveTask( Task( "TITLE1", "DESCRIPTION1",
            false, "id1" ) )
        tasksRepository.saveTask( Task( "TITLE2", "DESCRIPTION2",
            true, "id2" ) )

        val navController = mock( NavController::class.java )
        val scenario = launchFragmentInContainer<TasksFragment>( Bundle(), R.style.AppTheme )
        scenario.onFragment {
            Navigation.setViewNavController( it.view!!, navController )
        }

        onView( withId( R.id.tasks_list ) )
            .perform( RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant( withText( "TITLE1" ) ), click() ) )

        verify( navController ).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( "id1" )
        )
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() = runTest {
        val scenario = launchFragmentInContainer<TasksFragment>( Bundle(), R.style.AppTheme )
        val navController = mock( NavController::class.java )
        scenario.onFragment {
            Navigation.setViewNavController( it.view!!, navController )
        }

        onView( withId( R.id.add_task_fab ) ).perform( click() )
        verify( navController ).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null, getApplicationContext<Context>().getString( R.string.add_task )
            )
        )
    }
}