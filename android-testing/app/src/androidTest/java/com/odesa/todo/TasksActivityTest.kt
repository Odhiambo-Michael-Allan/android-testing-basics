package com.odesa.todo

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.odesa.todo.data.Task
import com.odesa.todo.data.source.TasksRepository
import com.odesa.todo.tasks.TasksActivity
import com.odesa.todo.util.DataBindingIdlingResource
import com.odesa.todo.util.EspressoIdlingResource
import com.odesa.todo.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E Test.
 */
@RunWith( AndroidJUnit4::class )
@LargeTest
class TasksActivityTest {

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var tasksRepository: TasksRepository

    @Before
    fun setup() {
        tasksRepository = ServiceLocator.provideTasksRepository(
            ApplicationProvider.getApplicationContext()
        )
        runBlocking {
            tasksRepository.deleteAllTasks()
        }

        /**
         * Idling resources tell Espresso that the app is idle or busy. This is needed when
         * operations are not scheduled in the main Looper ( for example when executed on a
         * different thread ).
         */
        IdlingRegistry.getInstance().register( EspressoIdlingResource.countingIdlingResource )
        IdlingRegistry.getInstance().register( dataBindingIdlingResource )
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
        IdlingRegistry.getInstance().unregister( EspressoIdlingResource.countingIdlingResource )
        IdlingRegistry.getInstance().unregister( dataBindingIdlingResource )
    }

    /**
     * runBlocking is used to wait for all suspend functions to finish before continuing with the
     * execution in the block. I'm using runBlocking instead of runBlockingTest because of a
     * bug -> https://github.com/Kotlin/kotlinx.coroutines/issues/1204
     */
    @Test
    fun editTask() = runBlocking {
        tasksRepository.saveTask( Task( "TITLE1", "DESCRIPTION" ) )

        // Start up the Tasks screen.
        val activityScenario = ActivityScenario.launch( TasksActivity::class.java )
        dataBindingIdlingResource.monitorActivity( activityScenario )

        // Click on the task on the list and verify that all the data is correct.
        onView( withText( "TITLE1" ) ).perform( click() )
        onView( withId( R.id.task_detail_title_text ) )
            .check( matches( withText( "TITLE1" ) ) )
        onView( withId( R.id.task_detail_description_text ) )
            .check( matches( withText( "DESCRIPTION" ) ) )
        onView( withId( R.id.task_detail_complete_checkbox ) )
            .check( matches( isNotChecked() ) )

        // Click on the edit button, edit, and save.
        onView( withId( R.id.edit_task_fab ) ).perform( click() )
        onView( withId( R.id.add_task_title_edit_text ) )
            .perform( replaceText( "NEW TITLE" ) )
        onView( withId( R.id.add_task_description_edit_text ) )
            .perform( replaceText( "NEW DESCRIPTION" ) )
        onView( withId( R.id.save_task_fab ) )
            .perform( click() )

        // Verify task is displayed on screen in the task list.
        onView( withText( "NEW TITLE" ) )
            .check( matches( isDisplayed() ) )
        // Verify previous task is not displayed.
        onView( withText( "TITLE1" ) )
            .check( doesNotExist() )
        activityScenario.close()
    }

    @Test
    fun createOneTask_deleteTask() {
        val activityScenario = ActivityScenario.launch( TasksActivity::class.java )
        dataBindingIdlingResource.monitorActivity( activityScenario )

        onView( withId( R.id.add_task_fab ) ).perform( click() )
        onView( withId( R.id.add_task_title_edit_text ) )
            .perform( typeText( "TITLE1" ), closeSoftKeyboard() )
        onView( withId( R.id.add_task_description_edit_text ) )
            .perform( typeText( "DESCRIPTION" ) )
        onView( withId( R.id.save_task_fab ) ).perform( click() )

        onView( withText( "TITLE1" ) ).perform( click() )
        onView( withId( R.id.menu_delete ) ).perform( click() )

        onView( withId( R.id.menu_filter ) ).perform( click() )
        onView( withText( R.string.nav_all ) ).perform( click() )
        onView( withText( "TITLE1" ) ).check( doesNotExist() )

        activityScenario.close()

    }
}