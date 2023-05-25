package ru.twilightsu.walletfs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.AppViewModelProvider
import ru.twilightsu.walletfs.ui.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAddScreen(
    modifier: Modifier = Modifier,
    accountViewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
){
    val accountUiState by accountViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = accountUiState.name,
            onValueChange = { accountViewModel.updateName(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(text = "Введите название счета")}
        )
        OutlinedTextField(
            value = accountUiState.balance,
            onValueChange = { accountViewModel.updateBalance(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Введите баланс счета")}
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Кредитная карта? ")
            Switch(checked = accountUiState.isCreditCard, onCheckedChange = { accountViewModel.updateIsCreditCard(it) })
        }
        OutlinedTextField(
            value = accountUiState.creditLimit,
            onValueChange = { accountViewModel.updateCreditLimit(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Введите кредитный лимит")},
            enabled = accountUiState.isCreditCard
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    accountViewModel.saveAccount()
                }
                navigateBack()
            },
            enabled = accountUiState.isFormValid
        ) {
            Text(text = "Сохранить")
        }
    }
}