package com.legstart.core_android

import androidx.lifecycle.LifecycleOwner
import com.legstart.core.BoundTask


interface SingleTask<T> {
    fun bindTo(lifecycleOwner: LifecycleOwner): BoundTask<T>
    fun cancel()
}