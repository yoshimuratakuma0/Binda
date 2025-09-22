package com.legstart.binda

import com.legstart.core.BoundTask

interface FruitRepository {
    suspend fun fetchFruits(): List<String>
}