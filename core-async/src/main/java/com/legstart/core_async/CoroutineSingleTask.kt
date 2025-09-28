package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.SingleTask
import com.legstart.core.TaskScope

class CoroutineSingleTask<T>(
    private val block: suspend () -> T
) : SingleTask<T> {
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return CoroutineBoundTask(
            taskScope = taskScope,
            block = block,
        )
    }
}
