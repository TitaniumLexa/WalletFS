package ru.twilightsu.walletfs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.twilightsu.walletfs.AccountRepository
import ru.twilightsu.walletfs.entities.Account
import ru.twilightsu.walletfs.entities.AccountWithBalance

class AccountsViewModel(private val accountRepository: AccountRepository): ViewModel() {
    val allAccountsWithBalance: LiveData<List<AccountWithBalance>> = accountRepository.getAllAccountWithBalance().asLiveData()
}

