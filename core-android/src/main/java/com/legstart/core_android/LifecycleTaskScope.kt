package com.legstart.core_android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible

class LifecycleTaskScope(
    private val lifecycleOwner: LifecycleOwner,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TaskScope {
    override fun launch(task: () -> Unit): Cancelable {
        val job = lifecycleOwner.lifecycleScope.launch(dispatcher) {
            runInterruptible {
                task()
            }
        }
        return object : Cancelable {
            override fun cancel() {
                job.cancel()
            }
        }
    }
}
