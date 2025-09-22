package com.legstart.core

interface TaskScope {
    fun launch(task: () -> Unit): Cancelable
}

interface Cancelable {
    fun cancel()
}