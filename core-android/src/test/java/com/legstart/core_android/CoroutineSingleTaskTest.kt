package com.legstart.core_android

import com.legstart.core_android.scopes.CoroutineTaskScope
import kotlinx.coroutines.CoroutineDispatcher
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
}