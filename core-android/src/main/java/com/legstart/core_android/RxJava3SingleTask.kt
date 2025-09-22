package com.legstart.core_android

import com.legstart.core.BoundTask
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

class RxJava3SingleTask<T : Any>(
    private val single: Single<T>
) : SingleTask<T> {
    private var disposable: Disposable? = null
    override fun bindTo(taskScope: TaskScope): BoundTask<T> {
        return object : BoundTask<T> {
            override fun start(
                onSuccess: (T) -> Unit,
                onError: (Throwable) -> Unit,
                onCancel: () -> Unit
            ) {
                disposable = single.subscribe(
                    { result: T -> onSuccess(result) },
                    { error: Throwable -> onError(error) }
                )
            }

            override fun cancel() {
                disposable?.dispose()
            }
        }
    }

    override fun cancel() {
        disposable?.dispose()
    }
}

