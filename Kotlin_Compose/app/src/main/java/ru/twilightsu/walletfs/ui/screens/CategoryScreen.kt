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
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.ui.viewmodels.CategoryViewModel
import ru.twilightsu.walletfs.ui.viewmodels.toCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    categoryViewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
) {
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = categoryUiState.name,
            onValueChange = { categoryViewModel.updateName(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(text = "Введите название категории") }
        )

        var expanded by remember { mutableStateOf(false) }

        var textOption: String by remember {
            mutableStateOf(enumValues<CategoryTypeEnum>()
                .find { it.value == categoryUiState.categoryTypeId }
                .toString())
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = textOption,
                readOnly = true,
                label = { Text(text = "Выберите тип категории") },
                onValueChange = {},
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = CategoryTypeEnum.EXPENSE.name) },
                    onClick = {
                        textOption = "Расходы" //CategoryTypeEnum.EXPENSE.name
                        expanded = false
                        categoryViewModel.updateCategoryTypeId(CategoryTypeEnum.EXPENSE.value)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
                DropdownMenuItem(
                    text = { Text(text = CategoryTypeEnum.INCOME.name) },
                    onClick = {
                        textOption = "Доходы" //CategoryTypeEnum.INCOME.name
                        expanded = false
                        categoryViewModel.updateCategoryTypeId(CategoryTypeEnum.INCOME.value)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    categoryViewModel.saveCategory()
                }
                navigateBack()
            },
            enabled = categoryUiState.isFormValid
        ) {
            Text(text = "Сохранить")
        }
    }
}