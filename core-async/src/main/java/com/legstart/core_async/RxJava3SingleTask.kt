package com.legstart.core_async

import com.legstart.core.BoundTask
import com.legstart.core.SingleTask
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Single

class RxJava3SingleTask<T : Any>(
    private val single: Single<T>,
) : SingleTask<T> {
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return RxJava3BoundTask(
            taskScope = taskScope,
            single = single,
        )
    }
}

