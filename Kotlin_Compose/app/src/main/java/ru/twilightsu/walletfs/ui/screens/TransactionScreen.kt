package ru.twilightsu.walletfs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.AppViewModelProvider
import ru.twilightsu.walletfs.TransactionRepository
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.ui.viewmodels.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
) {
    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val accounts =
        transactionViewModel.accountsList.observeAsState(arrayListOf()).value.map { account ->
            Pair(
                account.Id,
                account.Name
            )
        }
    val categories =
        transactionViewModel.typedCategoriesList.observeAsState(arrayListOf()).value.map { category ->
            Pair(
                category.Id,
                category.Name
            )
        }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DropdownMenu(
            list = accounts,
            updateMenuId = { it -> transactionViewModel.updateAccountId(it) },
            label = { Text(text = "Выберите счет") },
            selectedId = transactionUiState.accountId
        )
        DropdownMenu(
            list = categories,
            updateMenuId = { it -> transactionViewModel.updateCategoryId(it) },
            label = { Text(text = "Выберите категорию") },
            selectedId = transactionUiState.categoryId
        )

        OutlinedTextField(
            value = transactionUiState.amount,
            onValueChange = { transactionViewModel.updateAmount(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Введите сумму транзакции") }
        )

        OutlinedTextField(
            value = transactionUiState.comment,
            onValueChange = { transactionViewModel.updateComment(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(text = "Комментарий (необязательно)") }
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    transactionViewModel.saveTransaction()
                }
                navigateBack()
            },
            enabled = transactionUiState.isFormValid
        ) {
            Text(text = "Сохранить")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    label: @Composable (() -> Unit)?,
    list: List<Pair<Int, String>>,
    selectedId: Int,
    updateMenuId: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var textOption: String by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        textOption = list.find { pair -> pair.first == selectedId}?.second ?: ""
        TextField(
            modifier = Modifier.menuAnchor(),
            value = textOption,
            readOnly = true,
            label = label,
            onValueChange = {},
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.second) },
                    onClick = {
                        textOption = item.second
                        expanded = false

                        updateMenuId(item.first)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}