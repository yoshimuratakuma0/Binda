package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.SingleTask
import com.legstart.core.TaskScope

internal class CoroutineSingleTask<T>(
    private val block: suspend () -> T
) : SingleTask<T> {
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return CoroutineBoundTask(
            taskScope = taskScope,
            block = block,
        )
    }
}

fun <T> coroutineSingleTask(block: suspend () -> T): SingleTask<T> {
    return CoroutineSingleTask(block = block)
}