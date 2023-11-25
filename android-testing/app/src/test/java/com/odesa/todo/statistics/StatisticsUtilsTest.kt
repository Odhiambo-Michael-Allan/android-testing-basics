package com.odesa.todo.statistics

import com.odesa.todo.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        val tasks = emptyList<Task>()
        val result = getActiveAndCompletedStats( tasks )
        assertThat( result.completedTasksPercent, `is`( 0f ) )
        assertThat( result.completedTasksPercent, `is`( 0f ) )
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred() {
        val tasks = listOf (
            Task( "title", "desc", isCompleted = true )
        )
        val result = getActiveAndCompletedStats( tasks )
        assertThat( result.completedTasksPercent, `is`( 100f ) )
        assertThat( result.activeTasksPercent, `is`( 0f ) )
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsFortySixty() {
        val tasks = listOf (
            Task( "task1", "desc", isCompleted = true ),
            Task( "task2", "desc", isCompleted = true ),
            Task( "task3", "desc", isCompleted = false ),
            Task( "task4", "desc", isCompleted = false ),
            Task( "task5", "desc", isCompleted = false )
        )
        val result = getActiveAndCompletedStats( tasks )
        assertThat( result.completedTasksPercent, `is`( 40f ) )
        assertThat( result.activeTasksPercent, `is`( 60f ) )
    }

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        val tasks = listOf (
            Task( "title", "desc", isCompleted = false )
        )
        val result = getActiveAndCompletedStats( tasks )
        assertThat( result.completedTasksPercent, `is`( 0f ) )
        assertThat( result.activeTasksPercent, `is`( 100f ) )
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // When there's an error loading stats
        val result = getActiveAndCompletedStats( null )
        // Both active and completed tasks are 0
        assertThat( result.activeTasksPercent, `is`( 0f ) )
        assertThat( result.completedTasksPercent, `is`( 0f ) )
    }
}