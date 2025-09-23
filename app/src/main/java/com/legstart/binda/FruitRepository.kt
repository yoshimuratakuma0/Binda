package com.legstart.binda

import com.legstart.core.SingleTask

interface FruitRepository {
    fun fetchFruits(): SingleTask<List<String>>
}