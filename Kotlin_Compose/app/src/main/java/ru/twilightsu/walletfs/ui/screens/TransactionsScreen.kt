package ru.twilightsu.walletfs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.twilightsu.walletfs.AppViewModelProvider
import ru.twilightsu.walletfs.ui.viewmodels.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateTransaction: (Int, Int) -> Unit,
    navigateCategoryType: (Int) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateTransaction(0, viewModel.categoryTypeId) }) {
                Text(text = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            val index: Int = when (viewModel.categoryTypeId){
                3 -> 2
                2 -> 0
                1 -> 1
                else -> 0
            }

            var tabIndexState by remember { mutableStateOf(index) }

            TabRow(selectedTabIndex = tabIndexState) {
                Tab(
                    text = { Text(text = "Расходы") },
                    selected = tabIndexState == 0,
                    onClick = {
                        tabIndexState = 0
                        navigateCategoryType(2)
                    }
                )
                Tab(
                    text = { Text(text = "Доходы") },
                    selected = tabIndexState == 1,
                    onClick = {
                        tabIndexState = 1
                        navigateCategoryType(1)
                    }
                )
                Tab(
                    text = { Text(text = "Переводы") },
                    selected = tabIndexState == 2,
                    onClick = {
                        tabIndexState = 2
                        navigateCategoryType(3)
                    }
                )
            }
        }
    ) {
        val allCategories = viewModel.allTypedTransactions.observeAsState(arrayListOf()).value
        LazyColumn(modifier = Modifier.padding(it)) {
            items(items = allCategories, key = { it -> it.Id }) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        navigateTransaction(it.Id, viewModel.categoryTypeId)
                    }
                ) {
                    Text(
                        text = it.AccountName,
                        modifier = modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                    )
                    Text(
                        text = it.CategoryName,
                        modifier = modifier
                            .align(Alignment.Center)
                            .padding(10.dp)
                    )
                    Text(
                        text = it.Amount.toString(),
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