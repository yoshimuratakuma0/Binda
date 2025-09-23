package com.legstart.core_android

import com.legstart.core_android.scopes.CoroutineTaskScope
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class RxJava3SingleTaskTest {
    private lateinit var scheduler: TestScheduler
    private lateinit var taskScope: CoroutineTaskScope

    @Before
    fun setup() {
        scheduler = TestScheduler()
        taskScope = CoroutineTaskScope(
            scope = TestScope(),
        )
    }

    @Test
    fun `RxJava3SingleTask should execute the task and return result`() {
        // Given
        val expectedResult = "Hello, RxJava3!"
        val singleTask = RxJava3SingleTask(
            single = Single
                .timer(10, TimeUnit.MILLISECONDS, scheduler)
                .map { expectedResult },
            scheduler = scheduler,
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
}