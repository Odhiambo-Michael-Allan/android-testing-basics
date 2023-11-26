package com.odesa.todo

import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
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

@RunWith( AndroidJUnit4::class )
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        tasksRepository = ServiceLocator.provideTasksRepository( getApplicationContext() )
        IdlingRegistry.getInstance().register( EspressoIdlingResource.countingIdlingResource )
        IdlingRegistry.getInstance().register( dataBindingIdlingResource )
    }

    @After
    fun cleanup() {
        ServiceLocator.resetRepository()
        IdlingRegistry.getInstance().unregister( EspressoIdlingResource.countingIdlingResource )
        IdlingRegistry.getInstance().unregister( dataBindingIdlingResource )
    }

    @Test
    fun tasksScreen_clickOnDrawerIcon_OpensNavigation() {
        val activityScenario = ActivityScenario.launch( TasksActivity::class.java )
        dataBindingIdlingResource.monitorActivity( activityScenario )

        // Check that left drawer is closed at startup.
        onView( withId( R.id.drawer_layout ) )
            .check( matches( isClosed( Gravity.START ) ) )

        // Open drawer by clicking drawer icon.
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription() )
        ).perform( click() )

        // Check if drawer is open.
        onView( withId( R.id.drawer_layout ) )
            .check( matches( isOpen( Gravity.START ) ) )

        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task( "Up Button", "Description" )
        tasksRepository.saveTask( task )

        val activityScenario = ActivityScenario.launch( TasksActivity::class.java )
        dataBindingIdlingResource.monitorActivity( activityScenario )

        // Click on the task on the list
        onView( withText( "Up Button" ) )
            .perform( click() )

        // Click on the edit task button
        onView( withId( R.id.edit_task_fab ) )
            .perform( click() )

        // Confirm that if we click Up button once, we end up back at the details page
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform( click() )
        onView( withId( R.id.task_detail_title_text ) ).check( matches( isDisplayed() ) )

        // Confirm that if we click Up button a second time, we end up back at the home screen
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform( click() )
        onView( withId( R.id.tasks_container_layout ) ).check( matches( isDisplayed() ) )

        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task( "Back button", "Description" )
        tasksRepository.saveTask(task)

        // Start Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on the task on the list.
        onView( withText( "Back button" ) ).perform( click() )

        // 2. Click on the Edit task button.
        onView( withId( R.id.edit_task_fab ) ).perform( click() )

        // 3. Confirm that if we click Back once, we end up back at the task details page.
        pressBack()
        onView( withId( R.id.task_detail_title_text ) ).check( matches( isDisplayed() ) )

        // 4. Confirm that if we click Back a second time, we end up back at the home screen.
        pressBack()
        onView( withId( R.id.tasks_container_layout ) ).check( matches( isDisplayed() ) )

        // When using ActivityScenario.launch(), always call close()
        activityScenario.close()
    }
}

fun < T: Activity> ActivityScenario<T>.getToolbarNavigationContentDescription(): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>( R.id.toolbar )
            .navigationContentDescription as String
    }
    return description
}