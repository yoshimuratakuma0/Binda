package com.legstart.core_async

import com.legstart.core_async.scopes.RxJava3TaskScope
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineSingleTaskWithRxJava3TaskScopeTest {

    private lateinit var scheduler: TestScheduler
    private lateinit var taskScope: RxJava3TaskScope

    private lateinit var disposableContainer: CompositeDisposable

    @Before
    fun setup() {
        disposableContainer = CompositeDisposable()
        scheduler = TestScheduler()
        taskScope = RxJava3TaskScope(
            disposableContainer = disposableContainer,
            scheduler = scheduler,
        )
    }

    @Test
    fun `CoroutineSingleTask should execute the task and return result`() = runTest {
        // Given
        val expectedResult = "Hello, World!"
        val singleTask = CoroutineSingleTask {
            // Simulate some work
            delay(10)
            expectedResult
        }

        // When
        val boundTask = singleTask.bindTo(taskScope)
        var actualResult: String? = null
        var error: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> actualResult = result },
            onError = { throwable -> error = throwable },
            onCancel = { isCancelled = true }
        )
        scheduler.triggerActions()

        // Then
        Assert.assertEquals(
            expectedResult,
            actualResult,
        )
        Assert.assertNull(error)
        Assert.assertFalse(isCancelled)
    }

    @Test
    fun `CoroutineSingleTask should handle exception`() = runTest {
        // Given
        val expected = IllegalStateException("Test Exception")
        val singleTask = CoroutineSingleTask {
            // Simulate some work that throws an exception
            delay(10)
            throw expected
        }

        // When
        val boundTask = singleTask.bindTo(taskScope)
        var throwable: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> },
            onError = { t -> throwable = t },
            onCancel = { isCancelled = true }
        )

        scheduler.triggerActions()


        // Then
        Assert.assertEquals(expected, throwable)
        Assert.assertFalse(isCancelled)
    }

    @Test
    fun `CoroutineSingleTask should handle cancellation`() = runTest {
        // Given
        val singleTask = CoroutineSingleTask {
            // Simulate some work
            delay(10)
            throw kotlinx.coroutines.CancellationException()
        }

        // When
        val boundTask = singleTask.bindTo(taskScope)
        var error: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> },
            onError = { t -> error = t },
            onCancel = {
                isCancelled = true
            }
        )
        scheduler.triggerActions()

        // Then
        Assert.assertNull(error)
        Assert.assertEquals(true, isCancelled)
        Assert.assertEquals(true, boundTask.isCancelled)
    }

    @Test
    fun `CoroutineSingleTask cancellation should cancel the task`() = runTest {
        // Given
        var taskStarted = false
        var taskCompleted = false
        val singleTask = CoroutineSingleTask {
            taskStarted = true
            // Simulate some blocking work
            delay(300)
            taskCompleted = true
        }

        // When
        val ioCoroutineTaskScope = RxJava3TaskScope(
            scheduler = Schedulers.io(),
            disposableContainer = CompositeDisposable(),
        )
        val boundTask = singleTask.bindTo(ioCoroutineTaskScope)
        var error: Throwable? = null

        boundTask.start(
            onSuccess = { result -> },
            onError = { t -> error = t },
            onCancel = {
                // No-op
            }
        )
        delay(100) // Ensure the task has started
        boundTask.cancel()

        Assert.assertEquals(true, taskStarted)
        Assert.assertEquals(false, taskCompleted)
        Assert.assertNull(error)
        Assert.assertEquals(true, boundTask.isCancelled)
    }
}