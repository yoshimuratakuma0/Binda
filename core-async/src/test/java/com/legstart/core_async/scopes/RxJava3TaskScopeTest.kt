package com.legstart.core_async.scopes

import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RxJava3TaskScopeTest {
    private lateinit var taskScope: RxJava3TaskScope

    @Before
    fun setup() {
        taskScope = RxJava3TaskScope(
            scheduler = Schedulers.trampoline(),
        )
    }

    @Test
    fun `task should be executed`() {
        var taskExecuted = false
        val task = { taskExecuted = true }

        taskScope.launch(task)

        assertTrue("Task should be executed", taskExecuted)
    }

    @Test
    fun `cancel should cancel the task`() {
        var taskStarted = false
        var taskCompleted = false
        val task = {
            taskStarted = true
            // Simulate long-running task
            Thread.sleep(10)
            taskCompleted = true
        }

        val ioTaskScope = RxJava3TaskScope(
            scheduler = Schedulers.io(),
        )
        val cancelable = ioTaskScope.launch(task)
        cancelable.cancel()

        Thread.sleep(30)

        // Then
        assertTrue("Task should have started", taskStarted)
        assertTrue("Task should not be completed", !taskCompleted)
        assertTrue("Task should be cancelled", cancelable.isCancelled)
    }

    @Test
    fun `multiple tasks should be executed`() {
        var task1Executed = false
        var task2Executed = false

        val task1 = { task1Executed = true }
        val task2 = { task2Executed = true }

        taskScope.launch(task1)
        taskScope.launch(task2)

        assertTrue("Task 1 should be executed", task1Executed)
        assertTrue("Task 2 should be executed", task2Executed)
    }
}