package ru.twilightsu.walletfs.entities

import androidx.room.Dao
import androidx.room.Entity
import java.math.BigDecimal

@Entity
data class Budget (
    val Id: Int,
    val Amount: BigDecimal,
    val CategoryId: Int
)

@Dao
interface BudgetDao {

}
