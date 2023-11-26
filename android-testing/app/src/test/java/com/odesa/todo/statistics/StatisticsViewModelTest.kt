package com.odesa.todo.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.odesa.todo.MainCoroutineRule
import com.odesa.todo.data.source.FakeTasksRepository
import com.odesa.todo.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    private lateinit var tasksRepository: FakeTasksRepository

    @Before
    fun setup() {
        tasksRepository = FakeTasksRepository()
        statisticsViewModel = StatisticsViewModel( tasksRepository )
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        tasksRepository.setReturnError( true )
        statisticsViewModel.refresh()

        assertThat( statisticsViewModel.empty.getOrAwaitValue(), `is`( true ) )
        assertThat( statisticsViewModel.error.getOrAwaitValue(), `is`( true ) )
    }

//    @Test
//    fun loadTasks_loading() {
//        mainCoroutineRule.pauseDispatcher()
//        statisticsViewModel.refresh()
//        assertThat( statisticsViewModel.dataLoading.getOrAwaitValue(), `is`( true ) )
//        mainCoroutineRule.resumeDispatcher()
//        assertThat( statisticsViewModel.dataLoading.getOrAwaitValue(), `is`( false ) )
//    }
}