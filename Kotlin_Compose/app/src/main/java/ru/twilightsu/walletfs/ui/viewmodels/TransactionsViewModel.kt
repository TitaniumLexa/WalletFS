package ru.twilightsu.walletfs.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.twilightsu.walletfs.TransactionRepository
import ru.twilightsu.walletfs.entities.TransactionWithAccountAndCategoryNames

class TransactionsViewModel(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val categoryTypeId: Int = checkNotNull(savedStateHandle["categoryTypeId"])
    val allTypedTransactions: LiveData<List<TransactionWithAccountAndCategoryNames>> = transactionRepository.getAllTyped(categoryTypeId).asLiveData()
}