package com.odesa.todo.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Test.
 */
@MediumTest
@RunWith( AndroidJUnit4::class )
class TaskDetailFragmentTest {

    private lateinit var tasksRepository: TasksRepository

    @Before
    fun setup() {
        tasksRepository = FakeAndroidTasksRepository()
        ServiceLocator.tasksRepository = tasksRepository
    }

    @After
    fun cleanup() = runTest { ServiceLocator.resetRepository() }

    @Test
    fun activeTaskDetails_DisplayedInUi() = runTest {
        val activeTask = Task( "Active Task", "AndroidX Rocks", false )
        tasksRepository.saveTask( activeTask )
        val bundle = TaskDetailFragmentArgs( activeTask.id ).toBundle()
        launchFragmentInContainer<TaskDetailFragment>( bundle, R.style.AppTheme )

        onView( withId( R.id.task_detail_title_text ) ).check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_title_text ) )
            .check( matches( withText( "Active Task" ) ) )
        onView( withId( R.id.task_detail_description_text ) )
            .check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_description_text ) )
            .check( matches( withText( "AndroidX Rocks" ) ) )
        onView( withId( R.id.task_detail_complete_checkbox ) )
            .check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_complete_checkbox ) )
            .check( matches( isNotChecked() ) )
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() = runTest {
        val completedTask = Task( "Completed Task", "AndroidX Rocks!!",
            true )
        tasksRepository.saveTask( completedTask )
        val bundle = TaskDetailFragmentArgs( completedTask.id ).toBundle()
        launchFragmentInContainer<TaskDetailFragment>( bundle, R.style.AppTheme )

        onView( withId( R.id.task_detail_title_text ) )
            .check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_title_text ) )
            .check( matches( withText( "Completed Task" ) ) )

        onView( withId( R.id.task_detail_description_text ) )
            .check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_description_text ) )
            .check( matches( withText( "AndroidX Rocks!!" ) ) )

        onView( withId( R.id.task_detail_complete_checkbox ) )
            .check( matches( isDisplayed() ) )
        onView( withId( R.id.task_detail_complete_checkbox ) )
            .check( matches( isChecked() ) )
    }
}