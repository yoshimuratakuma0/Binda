package com.legstart.binda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.legstart.binda.ui.theme.BindaTheme

class MainActivity : ComponentActivity() {
    private val coroutineViewModel by lazy {
        CoroutineViewModel(RxJava3FruitRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BindaTheme {
                val fruitsByCoroutine by coroutineViewModel.fruits.collectAsStateWithLifecycle()
                val isLoadingByCoroutine by coroutineViewModel.isLoading.collectAsStateWithLifecycle()
                MainScreen(
                    isLoadingByCoroutine = isLoadingByCoroutine,
                    fruitsByCoroutine = fruitsByCoroutine,
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    isLoadingByCoroutine: Boolean,
    fruitsByCoroutine: List<String>,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            item {
                Text(text = "Fruits from Coroutine")
            }
            item {
                FruitsScreen(
                    isLoading = isLoadingByCoroutine,
                    fruits = fruitsByCoroutine,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun FruitsScreen(
    isLoading: Boolean,
    fruits: List<String>,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .height(100.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier)
        }
    }
    Column(
        modifier = modifier,
    ) {
        fruits.forEach {
            Text(text = it)
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    BindaTheme {
        MainScreen(
            isLoadingByCoroutine = false,
            fruitsByCoroutine = listOf("banana", "apple", "orange"),
        )
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    BindaTheme {
        FruitsScreen(
            fruits = emptyList(),
            isLoading = true,
        )
    }
}