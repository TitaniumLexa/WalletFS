package ru.twilightsu.walletfs

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.entities.Account
import ru.twilightsu.walletfs.entities.AccountDao
import ru.twilightsu.walletfs.entities.AccountWithBalance
import ru.twilightsu.walletfs.entities.BigDecimalTypeConverter
import ru.twilightsu.walletfs.entities.Category
import ru.twilightsu.walletfs.entities.CategoryDao
import ru.twilightsu.walletfs.entities.CategoryType
import ru.twilightsu.walletfs.entities.CategoryTypeDao
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.entities.CategoryTypeEnumConverter
import ru.twilightsu.walletfs.entities.Transaction
import ru.twilightsu.walletfs.entities.TransactionDao
import ru.twilightsu.walletfs.entities.TransactionWithAccountAndCategoryNames
import ru.twilightsu.walletfs.entities.ZonedDateTimeTypeConverter
import ru.twilightsu.walletfs.entities.toCategoryType
import java.math.BigDecimal

@Database(
    entities = [Account::class, Category::class, Transaction::class, CategoryType::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(
    BigDecimalTypeConverter::class,
    ZonedDateTimeTypeConverter::class,
    CategoryTypeEnumConverter::class
)
abstract class WalletRoomDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryTypeDao(): CategoryTypeDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: WalletRoomDatabase? = null

        fun getDatabase(context: Context): WalletRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    WalletRoomDatabase::class.java,
                    "wallet_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(seedDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private fun seedDatabaseCallback(context: Context): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    seedDb()
                }

                override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                    super.onDestructiveMigration(db)

                    seedDb()
                }

                private fun seedDb(){
                    val walletDb = getDatabase(context)
                    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
                    scope.launch {
                        seedCategoryType(walletDb.categoryTypeDao())
                        seedCategory(walletDb.categoryDao())
                    }
                }

                private suspend fun seedCategoryType(categoryTypeDao: CategoryTypeDao) {
                    categoryTypeDao.insert(CategoryTypeEnum.EXPENSE.toCategoryType())
                    categoryTypeDao.insert(CategoryTypeEnum.INCOME.toCategoryType())
                    categoryTypeDao.insert(CategoryTypeEnum.TRANSFER.toCategoryType())
                    categoryTypeDao.insert(CategoryTypeEnum.BALANCE.toCategoryType())
                }

                private suspend fun seedCategory(categoryDao: CategoryDao) {
                    categoryDao.insert(
                        Category(
                            1,
                            CategoryTypeEnum.TRANSFER.name,
                            CategoryTypeEnum.TRANSFER.value
                        )
                    )
                    categoryDao.insert(
                        Category(
                            2,
                            CategoryTypeEnum.BALANCE.name,
                            CategoryTypeEnum.BALANCE.value
                        )
                    )
                }

            }
        }


    }
}

class AccountRepository(private val accountDao: AccountDao) {
    val allAccounts: Flow<List<Account>> = accountDao.getAll()

    fun getAllAccountWithBalance() : Flow<List<AccountWithBalance>> = accountDao.getAllWithBalance()

    fun getAccountWithBalance(id: Int) : Flow<AccountWithBalance?> = accountDao.getAccountWithBalance(id)

    @WorkerThread
    suspend fun insert(account: Account): Int {
        val rowId = accountDao.insert(account)
        return accountDao.getByRowId(rowId)
    }

    fun get(id: Int): Flow<Account?> {
        return accountDao.get(id)
    }
}

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAll(): Flow<List<Category>> = categoryDao.getAll()

    fun getAllTyped(categoryTypeId: Int): Flow<List<Category>> =
        categoryDao.getAllTyped(categoryTypeId)

    fun get(id: Int): Flow<Category?> = categoryDao.get(id)

    @WorkerThread
    suspend fun insert(category: Category) = categoryDao.insert(category)
}

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun get(id: Int): Flow<Transaction?> = transactionDao.get(id)

    fun getAllTyped(categoryTypeId: Int): Flow<List<TransactionWithAccountAndCategoryNames>> =
        transactionDao.getAllTyped(categoryTypeId)

    @WorkerThread
    suspend fun insert(transaction: Transaction, categoryTypeId: Int) {
        if (categoryTypeId == CategoryTypeEnum.EXPENSE.value)
        {
            transactionDao.insert(transaction.copy(Amount = transaction.Amount.negate()))
        }
        else
        {
            transactionDao.insert(transaction)
        }
    }
}