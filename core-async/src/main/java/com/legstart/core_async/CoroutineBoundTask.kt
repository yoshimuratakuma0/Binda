package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import kotlinx.coroutines.runBlocking

class CoroutineBoundTask<T>(
    private val taskScope: TaskScope,
    private val block: suspend () -> T,
) : BoundTask<T> {
    private lateinit var cancelable: Cancelable
    override val isCancelled: Boolean
        get() = cancelable.isCancelled

    override fun start(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onCancel: () -> Unit,
    ) {
        cancelable = taskScope.launch {
            try {
                val result = runBlocking { block() }
                onSuccess(result)
            } catch (_: kotlin.coroutines.cancellation.CancellationException) {
                cancelable.cancel()
                onCancel()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    override fun cancel() {
        cancelable.cancel()
    }
}