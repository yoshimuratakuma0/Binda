package com.legstart.core_async.scopes

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RxJava3TaskScopeTest {
    private lateinit var taskScope: RxJava3TaskScope

    private lateinit var disposableContainer: CompositeDisposable

    @Before
    fun setup() {
        disposableContainer = CompositeDisposable()
        taskScope = RxJava3TaskScope(
            compositeDisposable = disposableContainer,
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
            Thread.sleep(100)
            taskCompleted = true
        }

        val ioTaskScope = RxJava3TaskScope(
            compositeDisposable = CompositeDisposable(),
            scheduler = Schedulers.io(),
        )
        val cancelable = ioTaskScope.launch(task)
        Thread.sleep(5)
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

    @Test
    fun `disposables should be disposed after scope is canceled`() {
        val cancelable1 = taskScope.launch { Thread.sleep(10) }
        val cancelable2 = taskScope.launch { Thread.sleep(10) }
        taskScope.cancel()

        assertTrue("Cancelable 1 should be cancelled", cancelable1.isCancelled)
        assertTrue("Cancelable 2 should be cancelled", cancelable2.isCancelled)
    }
}