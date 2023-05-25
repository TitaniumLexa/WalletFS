package ru.twilightsu.walletfs

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.twilightsu.walletfs.ui.AccountViewModel
import ru.twilightsu.walletfs.ui.AccountsViewModel
import ru.twilightsu.walletfs.ui.viewmodels.CategoriesViewModel
import ru.twilightsu.walletfs.ui.viewmodels.CategoryViewModel
import ru.twilightsu.walletfs.ui.viewmodels.TransactionViewModel
import ru.twilightsu.walletfs.ui.viewmodels.TransactionsViewModel

class WalletApplication : Application() {
    lateinit var container: WalletDataContainer
    override fun onCreate() {
        super.onCreate()
        container = WalletDataContainer(this)
    }
}

class WalletDataContainer(private val context: Context) {
    val accountRepository: AccountRepository by lazy {
        AccountRepository(WalletRoomDatabase.getDatabase(context).accountDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(WalletRoomDatabase.getDatabase(context).categoryDao())
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(WalletRoomDatabase.getDatabase(context).transactionDao())
    }
}

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AccountViewModel(
                this.createSavedStateHandle(),
                walletApplication().container.accountRepository,
                walletApplication().container.transactionRepository
            )
        }

        initializer {
            AccountsViewModel(walletApplication().container.accountRepository)
        }

        initializer {
            CategoryViewModel(
                this.createSavedStateHandle(),
                walletApplication().container.categoryRepository
            )
        }

        initializer {
            CategoriesViewModel(
                this.createSavedStateHandle(),
                walletApplication().container.categoryRepository
            )
        }

        initializer {
            TransactionViewModel(
                this.createSavedStateHandle(),
                walletApplication().container.transactionRepository,
                walletApplication().container.categoryRepository,
                walletApplication().container.accountRepository
            )
        }

        initializer {
            TransactionsViewModel(
                this.createSavedStateHandle(),
                walletApplication().container.transactionRepository
            )
        }
    }
}

fun CreationExtras.walletApplication(): WalletApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WalletApplication)
