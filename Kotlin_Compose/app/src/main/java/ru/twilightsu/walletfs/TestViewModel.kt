package ru.twilightsu.walletfs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TestUiState())

    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    var testInput by mutableStateOf("")
        private set

    fun Increment() {
        val count = _uiState.value.count
        _uiState.value = TestUiState(count+1)
    }

    fun UpdateTestInput(input: String){
        testInput = input
    }
}

data class TestUiState(
    val count: Int = 0
)