package com.legstart.binda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.legstart.core_android.scopes.CoroutineTaskScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    fruitRepository: FruitRepository,
) : ViewModel() {
    private val taskScope = CoroutineTaskScope(
        scope = viewModelScope,
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
}