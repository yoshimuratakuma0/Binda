package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Single

class RxJava3BoundTask<T : Any>(
    private val taskScope: TaskScope,
    private val single: Single<T>,
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
                val result = single.blockingGet()
                onSuccess(result)
            } catch (_: java.util.concurrent.CancellationException) {
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