package com.legstart.core_android

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CoroutineTaskScopeTest {
    private lateinit var coroutineTaskScope: CoroutineTaskScope

    @Before
    fun setup() {
        coroutineTaskScope = CoroutineTaskScope(
            scope = CoroutineScope(Dispatchers.IO),
            dispatcher = Dispatchers.IO,
        )
    }

    @Test
    fun `launch should return non-null cancelable`() = runTest {
        // Given
        val task = { /* empty task */ }

        // When
        val cancelable = coroutineTaskScope.launch(task)

        // Then
        assertNotNull("Cancelable should not be null", cancelable)
    }

    @Test
    fun `task should be executed`() = runTest {
        // Given
        var taskExecuted = false
        val task = { taskExecuted = true }

        // When
        coroutineTaskScope.launch(task)

        // IOディスパッチャーで実行されるため、タスク完了を待つ
        Thread.sleep(10)

        // Then
        assertTrue("Task should be executed", taskExecuted)
    }

    @Test
    fun `cancel should cancel the job`() = runTest {
        var taskStarted = false
        var taskCompleted = false
        val task = {
            taskStarted = true
            // ブロッキング処理をシミュレート
            Thread.sleep(10)
            taskCompleted = true
        }

        val cancelable = coroutineTaskScope.launch(task)
        // タスクが開始されるのを待つ
        Thread.sleep(5)
        cancelable.cancel()
        Thread.sleep(20)

        // Then - runInterruptibleによりキャンセルが機能することを確認
        assertTrue("Task should have started", taskStarted)
        assertEquals(
            "Task should not complete after cancel",
            false,
            taskCompleted,
        )
    }

    @Test
    fun `cancel should not throw exception`() = runTest {
        // Given
        val task = { /* empty task */ }
        val cancelable = coroutineTaskScope.launch(task)

        // When & Then
        try {
            cancelable.cancel()
        } catch (e: Exception) {
            fail("Cancel should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `multiple cancel calls should not throw exception`() = runTest {
        // Given
        val task = { /* empty task */ }
        val cancelable = coroutineTaskScope.launch(task)

        // When & Then
        try {
            cancelable.cancel()
            cancelable.cancel() // Should not cause issues
        } catch (e: Exception) {
            fail("Multiple cancel calls should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `scope cancellation should cancel all jobs`() = runTest {
        // IOディスパッチャーを使用
        val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val ioTaskScope = CoroutineTaskScope(ioScope, Dispatchers.IO)

        var task1Started = false
        var task1Completed = false
        var task2Started = false
        var task2Completed = false

        val task1 = {
            task1Started = true
            Thread.sleep(10)
            task1Completed = true
        }
        val task2 = {
            task2Started = true
            Thread.sleep(10)
            task2Completed = true
        }

        // When
        ioTaskScope.launch(task1)
        ioTaskScope.launch(task2)

        // タスクが開始されるのを待つ
        Thread.sleep(5)
        ioScope.cancel()
        Thread.sleep(20)

        // Then - スコープがキャンセルされたため、タスクは完了しないはず
        assertTrue("Task 1 should have started", task1Started)
        assertTrue("Task 2 should have started", task2Started)
        assertEquals("Task 1 should not complete after scope cancel", false, task1Completed)
        assertEquals("Task 2 should not complete after scope cancel", false, task2Completed)
    }

    @Test
    fun `multiple cancel calls should be safe`() = runTest {
        // Given
        val task = { /* empty task */ }
        val cancelable = coroutineTaskScope.launch(task)

        // When & Then
        try {
            cancelable.cancel()
            cancelable.cancel() // 複数回のキャンセルが安全であることを確認
            cancelable.cancel() // さらにもう一度

            assertTrue("Multiple cancel calls should be safe", true)
        } catch (e: Exception) {
            fail("Multiple cancel calls should not throw exception: ${e.message}")
        }
    }
}
