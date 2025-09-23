package com.legstart.core

interface TaskScope {
    fun launch(task: () -> Unit): Cancelable
}

interface Cancelable {
    val isCancelled: Boolean
    fun cancel()
}