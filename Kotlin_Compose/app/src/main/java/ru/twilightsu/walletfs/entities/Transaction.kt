package ru.twilightsu.walletfs.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.ZonedDateTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("transaction_id") val Id: Int,
    @ColumnInfo("account_id") val AccountId: Int,
    @ColumnInfo("category_id") val CategoryId: Int,
    @ColumnInfo("comment") val Comment: String,
    @ColumnInfo("amount") val Amount: BigDecimal,
    @ColumnInfo("date") val Date: ZonedDateTime
)

data class TransactionWithAccountAndCategoryNames(
    @ColumnInfo("transaction_id") val Id: Int,
    @ColumnInfo("account_id") val AccountId: Int,
    @ColumnInfo("category_id") val CategoryId: Int,
    @ColumnInfo("comment") val Comment: String,
    @ColumnInfo("amount") val Amount: BigDecimal,
    @ColumnInfo("date") val Date: ZonedDateTime,

    @ColumnInfo("account_name") val AccountName: String,
    @ColumnInfo("category_name") val CategoryName: String,
)

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT t.*, ac.account_name as account_name, ct.category_name as category_name 
        FROM transactions t 
        JOIN accounts ac ON t.account_id = ac.account_id
        JOIN categories ct ON t.category_id = ct.category_id
        WHERE ct.category_type_id == :categoryTypeId
        ORDER BY t.date DESC
        """
    )
    fun getAllTyped(categoryTypeId: Int): Flow<List<TransactionWithAccountAndCategoryNames>>

    @Query(
        """
            SELECT *
            FROM transactions t
            WHERE t.transaction_id == :id
        """
    )
    fun get(id: Int): Flow<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)
}

class ZonedDateTimeTypeConverter {
    @TypeConverter
    fun zonedDateTimeToString(input: ZonedDateTime): String {
        return input.toString()
    }

    @TypeConverter
    fun stringToZonedDateTime(input: String): ZonedDateTime {
        return ZonedDateTime.parse(input)
    }
}