package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.Cancelable
import com.legstart.core.SingleTask
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Single

class RxJava3SingleTask<T : Any>(
    private val single: Single<T>,
) : SingleTask<T> {
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return object : BoundTask<T> {
            private lateinit var cancelable: Cancelable
            override val isCancelled: Boolean
                get() = cancelable.isCancelled

            override fun start(
                onSuccess: (T) -> Unit,
                onError: (Throwable) -> Unit,
                onCancel: () -> Unit
            ) {
                cancelable = taskScope.launch {
                    try {
                        val result = single.blockingGet()
                        if (isCancelled) {
                            onCancel()
                            return@launch
                        }
                        onSuccess(result)
                    } catch (e: Exception) {
                        onError(e)
                        return@launch
                    }
                }
            }

            override fun cancel() {
                cancelable.cancel()
            }
        }
    }
}

