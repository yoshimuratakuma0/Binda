package com.legstart.binda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.legstart.core_android.CoroutineTaskScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    fruitRepository: FruitRepository,
) : ViewModel() {
    private val _fruits = MutableStateFlow<List<String>>(emptyList())
    private val taskScope = CoroutineTaskScope(
        scope = viewModelScope,
    )
    val fruits = _fruits.asStateFlow()

    init {
        val task = fruitRepository.fetchFruits()
        task.bindTo(taskScope).start(
            onSuccess = { result ->
                _fruits.value = result
            },
            onError = { error ->
                // Handle error
                val a = 0
            },
            onCancel = {
                // Handle cancellation
                val a = 0
            }
        )
    }
}