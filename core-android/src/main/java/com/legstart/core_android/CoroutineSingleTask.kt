package com.legstart.core_android

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.legstart.core.BoundTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class CoroutineSingleTask<T>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val block: suspend () -> T
) : SingleTask<T> {

    private var job: Job? = null

    override fun bindTo(lifecycleOwner: LifecycleOwner): BoundTask<T> {
        return object : BoundTask<T> {

            override fun start(
                onSuccess: (T) -> Unit,
                onError: (Throwable) -> Unit,
                onCancel: () -> Unit
            ) {
                val scope = lifecycleOwner.lifecycleScope

                val observer = object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        job?.cancel()
                        owner.lifecycle.removeObserver(this)
                        onCancel()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                job = scope.launch(dispatcher) {
                    try {
                        val result = block()
                        onSuccess(result)
                    } catch (_: CancellationException) {
                        onCancel()
                    } catch (e: Throwable) {
                        onError(e)
                    }
                }
            }

            override fun cancel() {
                job?.cancel()
            }
        }
    }

    override fun cancel() {
        job?.cancel()
    }
}