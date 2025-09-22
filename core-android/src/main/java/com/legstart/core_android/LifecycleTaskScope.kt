package com.legstart.core_android

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LifecycleTaskScope(
    private val lifecycleOwner: LifecycleOwner,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : TaskScope {
    private var job: Job? = null

    override fun launch(task: () -> Unit): Cancelable {
        val observer = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                job?.cancel()
                owner.lifecycle.removeObserver(this)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        job = lifecycleOwner.lifecycleScope.launch(dispatcher) {
            task()
        }
        return object : Cancelable {
            override fun cancel() {
                job?.cancel()
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}
