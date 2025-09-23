package com.legstart.core

interface SingleTask<T> {
    fun bindTo(taskScope: TaskScope): BoundTask<T>
}