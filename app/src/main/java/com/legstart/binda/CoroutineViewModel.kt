package com.legstart.binda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.legstart.core.TaskScope
import com.legstart.core_async.scopes.CoroutineTaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CoroutineViewModel(
    ioDispatcher: CoroutineDispatcher,
    fruitRepository: FruitRepository,
) : ViewModel() {
    private val taskScope: TaskScope = CoroutineTaskScope(
        scope = viewModelScope,
        dispatcher = ioDispatcher,
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
}