package ru.twilightsu.walletfs.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.AccountRepository
import ru.twilightsu.walletfs.CategoryRepository
import ru.twilightsu.walletfs.TransactionRepository
import ru.twilightsu.walletfs.entities.Account
import ru.twilightsu.walletfs.entities.Category
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.entities.Transaction
import java.math.BigDecimal
import java.time.ZonedDateTime

class TransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val transactionId: Int = checkNotNull(savedStateHandle["transactionId"])

    val categoryTypeId: Int = checkNotNull(savedStateHandle["categoryTypeId"])
    val typedCategoriesList: LiveData<List<Category>> = categoryRepository.getAllTyped(categoryTypeId).asLiveData() // ToDo: Retrive only id with name
    val accountsList: LiveData<List<Account>> = accountRepository.allAccounts.asLiveData() // ToDo: Retrive only id with name

    init {
        if (transactionId != 0){
            viewModelScope.launch {
                val transaction: Transaction = transactionRepository.get(transactionId)
                    .filterNotNull()
                    .first()

                if (categoryTypeId == CategoryTypeEnum.EXPENSE.value){
                    _uiState.value = transaction.copy(
                        Amount = transaction.Amount.negate()
                    ).toTransactionUiState()
                }
                else{
                    _uiState.value = transaction.toTransactionUiState()
                }
            }
        }
    }

    fun updateCategoryId(categoryId: Int){
        _uiState.value =_uiState.value.copy(
            categoryId = categoryId
        )
        isFormValid()
    }

    fun updateAccountId(accountId: Int){
        _uiState.value =_uiState.value.copy(
            accountId = accountId
        )
        isFormValid()
    }

    fun updateComment(comment: String){
        _uiState.value =_uiState.value.copy(
            comment = comment
        )
        isFormValid()
    }

    fun updateAmount(amount: String){
        _uiState.value =_uiState.value.copy(
            amount = amount
        )
        isFormValid()
    }

    fun isFormValid(){
        var formValid = true
        val state = _uiState.value

        if (state.accountId <= 0)
            formValid = false

        if (state.categoryId <= 0)
            formValid = false

        val amount = state.amount.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal(0))
            formValid = false

        _uiState.value = _uiState.value.copy(
            isFormValid = formValid
        )
    }

    suspend fun saveTransaction(){
        transactionRepository.insert(_uiState.value.toTransaction(), categoryTypeId)
    }
}

data class TransactionUiState(
    val id: Int = 0,
    val accountId: Int = 0,
    val categoryId: Int = 0,
    val comment: String = "",
    val amount: String = "",
    val date: ZonedDateTime? = null,

    val isFormValid: Boolean = false
)

fun Transaction.toTransactionUiState(): TransactionUiState = TransactionUiState(
    id = Id,
    accountId = AccountId,
    categoryId = CategoryId,
    comment = Comment,
    amount = Amount.toString(), // IF here to negate
    date = Date
)

fun TransactionUiState.toTransaction(): Transaction = Transaction(
    Id = id,
    AccountId = accountId,
    CategoryId = categoryId,
    Comment = comment,
    Amount = amount.toBigDecimalOrNull() ?: BigDecimal(0),
    Date = date ?: ZonedDateTime.now()
)