package com.legstart.binda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.legstart.binda.ui.theme.BindaTheme

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        CoroutineViewModel(RxJava3FruitRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BindaTheme {
                val fruits by viewModel.fruits.collectAsStateWithLifecycle()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                Screen(
                    fruits = fruits,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun Screen(
    fruits: List<String>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0x80000000)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier)
            }
        }
        LazyColumn(
            contentPadding = innerPadding,
        ) {
            items(fruits) { fruit ->
                Text(text = fruit)
            }
        }
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    BindaTheme {
        Screen(
            fruits = listOf("banana", "apple", "orange"),
            isLoading = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}