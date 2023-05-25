package ru.twilightsu.walletfs.entities

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.REAL
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "account_id") val Id: Int,
    @ColumnInfo(name="account_name") val Name: String,
    @ColumnInfo(name="is_credit_card") val IsCreditCard: Boolean,
    @ColumnInfo(name="credit_limit") val CreditLimit: BigDecimal?
)

data class AccountWithBalance(
    @ColumnInfo(name = "account_id") val Id: Int,
    @ColumnInfo(name="account_name") val Name: String,
    @ColumnInfo(name="is_credit_card") val IsCreditCard: Boolean,
    @ColumnInfo(name="credit_limit") val CreditLimit: BigDecimal?,

    @ColumnInfo(name="balance") val Balance: BigDecimal
)

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    fun getAll() : Flow<List<Account>>

    @Query("""
        SELECT trAc.account_id as account_id, ac.account_name as account_name, ac.is_credit_card as is_credit_card, ac.credit_limit as credit_limit , trAc.Balance as balance
        FROM (
            -- Суммируем операции после последнего указания баланса
            SELECT tr.account_id, SUM(Amount) as Balance
            FROM transactions as tr
            JOIN (
                -- Выбираем последную операцию указания баланса
                SELECT account_id, MAX(Date) as StartDate
                FROM transactions
                -- Выбираем категорию баланс
                WHERE category_id = (SELECT category_id FROM categories WHERE category_type_id = :balanceCategoryType LIMIT 1)
                GROUP BY account_id
                ) as startDateTr 
            ON tr.account_id = startDateTr.account_id
            WHERE Date >= StartDate
            GROUP BY tr.account_id
            ) as trAc
        JOIN accounts as ac ON trAc.account_id = ac.account_id
    """)
    fun getAllWithBalance(balanceCategoryType: Int = CategoryTypeEnum.BALANCE.value): Flow<List<AccountWithBalance>>

    @Query("""
        SELECT trAc.account_id as account_id, ac.account_name as account_name, ac.is_credit_card as is_credit_card, ac.credit_limit as credit_limit , trAc.Balance as balance
        FROM (
            -- Суммируем операции после последнего указания баланса
            SELECT tr.account_id, SUM(Amount) as Balance
            FROM transactions as tr
            JOIN (
                -- Выбираем последную операцию указания баланса
                SELECT account_id, MAX(Date) as StartDate
                FROM transactions
                -- Выбираем категорию баланс
                WHERE category_id = (SELECT category_id FROM categories WHERE category_type_id = :balanceCategoryType LIMIT 1)
                GROUP BY account_id
                ) as startDateTr 
            ON tr.account_id = startDateTr.account_id
            WHERE Date >= StartDate AND tr.account_id == :id 
            ) as trAc
        JOIN accounts as ac ON trAc.account_id = ac.account_id
    """)
    fun getAccountWithBalance(id: Int, balanceCategoryType: Int = CategoryTypeEnum.BALANCE.value) : Flow<AccountWithBalance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account) : Long

    @Query("SELECT * FROM accounts WHERE account_id = :id")
    fun get(id: Int) : Flow<Account>

    @Query("SELECT account_id FROM accounts WHERE rowid = :rowId")
    suspend fun getByRowId(rowId: Long) : Int

    @Query("SELECT SUM(credit_limit) as value FROM accounts")
    fun totalCreditLimit() : Flow<BigDecimal>
}

class BigDecimalTypeConverter {
    @TypeConverter
    fun bigDecimalToDouble(input: BigDecimal?): Double {
        return input?.toDouble() ?: 0.0
    }

    @TypeConverter
    fun doubleToBigDecimal(input: Double?): BigDecimal {
        if (input == null)
            return BigDecimal.ZERO
        return BigDecimal.valueOf(input) ?: BigDecimal.ZERO
    }

    @TypeConverter
    fun doubleToBigDecimalNullable(input: Double?): BigDecimal? {
        if (input == null)
            return null
        return BigDecimal.valueOf(input) ?: null
    }
}