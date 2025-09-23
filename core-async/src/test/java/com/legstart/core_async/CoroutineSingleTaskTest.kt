package com.legstart.core_async

import com.legstart.core_async.scopes.CoroutineTaskScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineSingleTaskTest {

    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var dispatcher: CoroutineDispatcher
    private lateinit var coroutineTaskScope: CoroutineTaskScope

    @Before
    fun setup() {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler)
        coroutineTaskScope = CoroutineTaskScope(
            scope = TestScope(scheduler),
            dispatcher = dispatcher,
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
        val boundTask = singleTask.bindTo(coroutineTaskScope)
        var actualResult: String? = null
        var error: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> actualResult = result },
            onError = { throwable -> error = throwable },
            onCancel = { isCancelled = true }
        )
        scheduler.advanceUntilIdle()

        // Then
        assertEquals(
            expectedResult,
            actualResult,
        )
        assertNull(error)
        assertFalse(isCancelled)
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
        val boundTask = singleTask.bindTo(coroutineTaskScope)
        var throwable: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> },
            onError = { t -> throwable = t },
            onCancel = { isCancelled = true }
        )

        scheduler.advanceUntilIdle()


        // Then
        assertEquals(expected, throwable)
        assertFalse(isCancelled)
    }

    @Test
    fun `CoroutineSingleTask should handle cancellation`() = runTest {
        // Given
        val singleTask = CoroutineSingleTask {
            // Simulate some work
            delay(10)
            throw CancellationException()
        }

        // When
        val boundTask = singleTask.bindTo(coroutineTaskScope)
        var error: Throwable? = null
        var isCancelled = false

        boundTask.start(
            onSuccess = { result -> },
            onError = { t -> error = t },
            onCancel = {
                isCancelled = true
            }
        )
        scheduler.advanceUntilIdle()

        // Then
        assertNull(error)
        assertEquals(true, isCancelled)
        assertEquals(true, boundTask.isCancelled)
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
        val ioCoroutineTaskScope = CoroutineTaskScope(
            scope = TestScope(scheduler),
            dispatcher = Dispatchers.IO,
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

        assertEquals(true, taskStarted)
        assertEquals(false, taskCompleted)
        assertNull(error)
        assertEquals(true, boundTask.isCancelled)
    }
}