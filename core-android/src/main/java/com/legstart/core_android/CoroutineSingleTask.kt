package com.legstart.core_android

import com.legstart.core.BoundTask
import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException

class CoroutineSingleTask<T>(
    private val block: suspend () -> T
) : SingleTask<T> {
    private var cancelable: Cancelable? = null

    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return object : BoundTask<T> {
            override fun start(
                onSuccess: (T) -> Unit,
                onError: (Throwable) -> Unit,
                onCancel: () -> Unit
            ) {
                cancelable?.cancel()

                cancelable = taskScope.launch {
                    try {
                        val result = runBlocking { block() }
                        onSuccess(result)
                    } catch (_: CancellationException) {
                        onCancel()
                    } catch (t: Throwable) {
                        onError(t)
                    }
                }
            }

            override fun cancel() {
                cancelable?.cancel()
            }
        }
    }

    override fun cancel() {
        cancelable?.cancel()
    }
}
