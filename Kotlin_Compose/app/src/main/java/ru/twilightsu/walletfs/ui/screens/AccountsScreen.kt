package ru.twilightsu.walletfs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.twilightsu.walletfs.AppViewModelProvider
import ru.twilightsu.walletfs.ui.AccountsViewModel
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateAddAccount: () -> Unit,
    navigateAccount: (Int) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = navigateAddAccount) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить счет")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        val accountsList = viewModel.allAccountsWithBalance.observeAsState(arrayListOf()).value
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF495D91))
                ){
                    Text(
                        text = "Название счета",
                        modifier = modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                    )
                    Text(
                        text = "Баланс",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )
                }
                Divider()
            }
            items(items = accountsList, key = { item -> item.Id }) { item ->
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        navigateAccount(item.Id)
                    }
                ) {
                    Text(
                        text = item.Name,
                        modifier = modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                    )

                    Text(
                        text = if (item.IsCreditCard) "${item.Balance}/${item.CreditLimit}" else "${item.Balance}",
                        modifier = modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )

                }
                Divider()
            }
        }
    }
}