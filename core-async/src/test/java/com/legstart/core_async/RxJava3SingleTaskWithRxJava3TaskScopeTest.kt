package com.legstart.core_async

import com.legstart.core_async.scopes.RxJava3TaskScope
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.TestScheduler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class RxJava3SingleTaskWithRxJava3TaskScopeTest {
    private lateinit var scheduler: TestScheduler
    private lateinit var taskScope: RxJava3TaskScope

    @Before
    fun setup() {
        scheduler = TestScheduler()
        taskScope = RxJava3TaskScope(
            compositeDisposable = CompositeDisposable(),
            scheduler = scheduler,
        )
    }

    @Test
    fun `RxJava3SingleTask should execute the task and return result`() {
        // Given
        val expectedResult = "Hello, RxJava3!"
        val singleTask = RxJava3SingleTask(
            single = Single.just(expectedResult),
        )

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

        // 時間を進めてtimerを発火させる
        scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)

        // Then
        assertEquals(
            "Expected result to be '$expectedResult' but was '$actualResult'",
            expectedResult,
            actualResult,
        )
        assertNull("Expected no error but got: $error", error)
        assertTrue("Task should not be cancelled", !isCancelled)
    }

    @Test
    fun `RxJava3SingleTask should handle error correctly`() {
        // Given
        val expectedError = RuntimeException("Test Error")
        val singleTask = RxJava3SingleTask<String>(
            single = Single
                .error(expectedError),
        )

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

        // 時間を進めてtimerを発火させる
        scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)

        // Then
        assertEquals("Expected no result but got: $actualResult", null, actualResult)
        assertEquals("Expected error to be '$expectedError' but was '$error'", expectedError, error)
        assertTrue("Task should not be cancelled", !isCancelled)
    }

    @Test
    fun `RxJava3SingleTask cancellation should dispose the task`() {
        // Given
        val expectedResult = "Hello, RxJava3!"
        val singleTask = RxJava3SingleTask(
            single = Single
                .timer(10, TimeUnit.MILLISECONDS, scheduler)
                .map { expectedResult },
        )

        // When
        val boundTask = singleTask.bindTo(taskScope)
        var actualResult: String? = null
        var error: Throwable? = null

        boundTask.start(
            onSuccess = { result -> actualResult = result },
            onError = { throwable -> error = throwable },
            onCancel = { }
        )
        boundTask.cancel()

        // Then
        assertEquals("Expected no result but got: $actualResult", null, actualResult)
        assertNull("Expected no error but got: $error", error)
        assertTrue("Task should be cancelled", boundTask.isCancelled)
    }
}