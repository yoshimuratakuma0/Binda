package com.legstart.binda

import androidx.lifecycle.ViewModel
import com.legstart.core_async.scopes.RxJava3TaskScope
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RxJava3ViewModel(
    fruitRepository: FruitRepository,
) : ViewModel() {

    private val taskScope = RxJava3TaskScope(
        disposableContainer = CompositeDisposable(),
    )
    private val _fruits = MutableStateFlow<List<String>>(emptyList())
    val fruits = _fruits.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        val task = fruitRepository.fetchFruits()
        _isLoading.value = true
        task.bindTo(taskScope).start(
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