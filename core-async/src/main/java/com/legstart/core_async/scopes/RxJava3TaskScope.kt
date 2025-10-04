package com.legstart.core_async.scopes

import com.legstart.core.Cancelable
import com.legstart.core.TaskScope
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

internal class RxJava3TaskScope(
    private val compositeDisposable: CompositeDisposable,
    private val scheduler: Scheduler,
) : TaskScope {
    override fun launch(task: () -> Unit): Cancelable {
        val worker = scheduler.createWorker()
        val disposable = worker.schedule {
            task()
        }
        compositeDisposable.add(disposable)
        return object : Cancelable {
            override val isCancelled: Boolean
                get() = disposable.isDisposed

            override fun cancel() {
                disposable.dispose()
            }
        }
    }

    override fun cancel() {
        compositeDisposable.clear()
    }
}

fun rxJava3TaskScope(
    compositionDisposable: CompositeDisposable,
    scheduler: Scheduler = Schedulers.io(),
): TaskScope {
    return RxJava3TaskScope(compositionDisposable, scheduler)
}