package com.legstart.core_android

import com.legstart.core.BoundTask
import com.legstart.core.TaskScope


interface SingleTask<T> {
    fun bindTo(taskScope: TaskScope): BoundTask<T>
}