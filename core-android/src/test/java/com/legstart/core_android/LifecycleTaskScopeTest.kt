package com.legstart.core_android

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LifecycleTaskScopeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var lifecycleOwner: TestLifecycleOwner
    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var lifecycleTaskScope: LifecycleTaskScope

    @Before
    fun setup() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        lifecycleOwner = TestLifecycleOwner()
        lifecycleTaskScope = LifecycleTaskScope(lifecycleOwner, testDispatcher)
    }

    @After
    fun tearDown() {
        lifecycleOwner.currentState = Lifecycle.State.DESTROYED
        Dispatchers.resetMain()
    }

    @Test
    fun `launch should add lifecycle observer`() = runTest {
        val task = { /* empty task */ }
        val initialObserverCount = lifecycleOwner.lifecycle.observerCount

        lifecycleTaskScope.launch(task)
        val addedObservers = lifecycleOwner.lifecycle.observerCount - initialObserverCount
        assertEquals(
            "Number of added observers should be one, but was $addedObservers",
            1,
            addedObservers,
        )
    }

    @Test
    fun `cancel should cancel the job`() = runTest {
        val targetScope = LifecycleTaskScope(
            lifecycleOwner,
            Dispatchers.IO,
        )
        var taskExecuted = false
        val task = {
            Thread.sleep(10)
            taskExecuted = true
        }

        val cancelable = targetScope.launch(task)
        cancelable.cancel()
        Thread.sleep(20)
        assertEquals(
            "Cancel should execute successfully",
            false,
            taskExecuted,
        )
    }

    @Test
    fun `The job should be waited for its completion on main dispatcher`() = runTest {
        val targetScope = LifecycleTaskScope(
            lifecycleOwner,
            Dispatchers.Main,
        )
        var taskExecuted = false
        val task = {
            Thread.sleep(10)
            taskExecuted = true
        }

        val cancelable = targetScope.launch(task)
        cancelable.cancel()
        Thread.sleep(20)
        assertEquals(
            "The task should be executed even after cancel on main dispatcher",
            true,
            taskExecuted,
        )
    }

    @Test
    fun `onDestroy should remove observer`() = runTest {
        // Given
        val task = { /* empty task */ }

        // When
        lifecycleTaskScope.launch(task)
        val observerCountAfterLaunch = lifecycleOwner.lifecycle.observerCount
        assertEquals(
            "Observer should be added after launch",
            1,
            observerCountAfterLaunch,
        )

        // Simulate lifecycle destruction
        lifecycleOwner.currentState = Lifecycle.State.DESTROYED

        // Then - onDestroyで少なくとも1つのオブザーバーが削除されることを確認
        assertEquals(
            "Observer should be removed after destroy",
            0,
            lifecycleOwner.lifecycle.observerCount,
        )
    }

    @Test
    fun `multiple launches should create different cancelables`() = runTest {
        // Given
        val task1 = { /* empty task 1 */ }
        val task2 = { /* empty task 2 */ }

        // When
        val cancelable1 = lifecycleTaskScope.launch(task1)
        val cancelable2 = lifecycleTaskScope.launch(task2)

        // Then
        assertNotEquals(
            "Cancelables should be different instances",
            cancelable1, cancelable2
        )
        assertNotNull("First cancelable should not be null", cancelable1)
        assertNotNull("Second cancelable should not be null", cancelable2)
    }

    @Test
    fun `multiple cancel calls should not throw exception`() = runTest {
        // Given
        val task = { /* empty task */ }
        val cancelable = lifecycleTaskScope.launch(task)

        // When & Then
        try {
            cancelable.cancel()
            cancelable.cancel() // Should not cause issues
        } catch (e: Exception) {
            fail("Multiple cancel calls should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `multiple cancel calls should be safe`() = runTest {
        // Given
        val task = { /* empty task */ }
        val cancelable = lifecycleTaskScope.launch(task)

        // When & Then
        try {
            cancelable.cancel()
            cancelable.cancel() // 複数回のキャンセルが安全であることを確認
            cancelable.cancel() // さらにもう一度

            // ライフサイクルを破棄した後でも安全であることを確認
            lifecycleOwner.currentState = Lifecycle.State.DESTROYED
            cancelable.cancel() // 破棄後のキャンセルも安全

            assertTrue("Multiple cancel calls should be safe", true)
        } catch (e: Exception) {
            fail("Multiple cancel calls should not throw exception: ${e.message}")
        }
    }
}
