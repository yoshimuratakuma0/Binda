package com.legstart.core_android

import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoroutineTaskScope(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TaskScope {
    override fun launch(
        task: () -> Unit,
    ): Cancelable {
        val job = scope.launch(dispatcher) {
            task()
        }
        return object : Cancelable {
            override fun cancel() {
                job.cancel()
            }
        }
    }
}