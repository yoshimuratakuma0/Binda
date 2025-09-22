package com.legstart.binda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel constructor(
    private val fruitRepository: FruitRepository,
): ViewModel() {
    private val _fruits = MutableStateFlow<List<String>>(emptyList())
    val fruits = _fruits.asStateFlow()

    init {
        viewModelScope.launch {
            _fruits.value = fruitRepository.fetchFruits()
        }
    }
}