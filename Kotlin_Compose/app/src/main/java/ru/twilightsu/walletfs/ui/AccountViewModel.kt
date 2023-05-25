package ru.twilightsu.walletfs.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.AccountRepository
import ru.twilightsu.walletfs.TransactionRepository
import ru.twilightsu.walletfs.entities.Account
import ru.twilightsu.walletfs.entities.AccountWithBalance
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.entities.Transaction
import java.math.BigDecimal
import java.time.ZonedDateTime

class AccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val accountId : Int = checkNotNull(savedStateHandle["accountId"])

    init {
        if (accountId > 0)
        {
            viewModelScope.launch {
                _uiState.value = accountRepository.getAccountWithBalance(accountId)
                    .filterNotNull()
                    .first()
                    .toAccountUiState()
            }
        }

    }

    fun updateName(value: String){
        _uiState.value = _uiState.value.copy(
            name = value
        )
        isFormValid()
    }

    fun updateIsCreditCard(value: Boolean){
        _uiState.value = _uiState.value.copy(
            isCreditCard = value
        )
        isFormValid()
    }

    fun updateBalance(value: String){
        _uiState.value = _uiState.value.copy(
            balance = value
        )
        isFormValid()
    }
    fun updateCreditLimit(value: String){
        _uiState.value = _uiState.value.copy(
            creditLimit = value
        )
        isFormValid()
    }

    fun isFormValid() {
        var formValid = true
        val state = _uiState.value

        if (state.name.isBlank())
            formValid = false

        if (state.balance.toBigDecimalOrNull() == null)
            formValid = false

        if (state.isCreditCard && state.creditLimit.toBigDecimalOrNull() == null)
            formValid = false

        _uiState.value = _uiState.value.copy(
            isFormValid = formValid
        )
    }

    suspend fun saveAccount(){
        val accountId: Int = accountRepository.insert(_uiState.value.toAccount())
        transactionRepository.insert(Transaction(
            Id = 0,
            AccountId = accountId,
            CategoryId = 2, // CategoryTypeId=4
            Comment = "Set balance",
            Amount = _uiState.value.balance.toBigDecimal(),
            Date = ZonedDateTime.now()
        ), CategoryTypeEnum.BALANCE.value)
    }
}

data class AccountUiState(
    val id: Int = 0,
    val name: String = "",
    val balance: String = "",
    val isCreditCard: Boolean = false,
    val creditLimit: String = "",

    val isFormValid: Boolean = false
)

fun AccountUiState.toAccount(): Account = Account(
    Id = id,
    Name = name,
    IsCreditCard = isCreditCard,
    CreditLimit = creditLimit.toBigDecimalOrNull()
)

fun Account.toAccountUiState(): AccountUiState = AccountUiState(
    id = Id,
    name = Name,
    isCreditCard = IsCreditCard,
    creditLimit = CreditLimit.toString()
)

fun AccountWithBalance.toAccountUiState(): AccountUiState = AccountUiState(
    id = Id,
    name = Name,
    isCreditCard = IsCreditCard,
    creditLimit = CreditLimit.toString(),
    balance = Balance.toString()
)