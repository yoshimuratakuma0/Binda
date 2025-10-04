package com.legstart.binda

import androidx.lifecycle.ViewModel
import com.legstart.core_async.scopes.rxJava3TaskScope
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RxJava3ViewModel(
    ioScheduler: Scheduler,
    fruitRepository: FruitRepository,
) : ViewModel() {

    private val taskScope = rxJava3TaskScope(
        scheduler = ioScheduler,
        compositionDisposable = CompositeDisposable(),
    )
    private val _fruits = MutableStateFlow<List<String>>(emptyList())
    val fruits = _fruits.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        val task = fruitRepository.fetchFruits()
        _isLoading.value = true
        val boundTask = task.bindTo(taskScope)
        boundTask.start(
            onSuccess = { result ->
                _fruits.value = result
                _isLoading.value = false
            },
            onError = { error ->
                _isLoading.value = false
            },
            onCancel = {
                _isLoading.value = false
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        taskScope.cancel()
    }
}