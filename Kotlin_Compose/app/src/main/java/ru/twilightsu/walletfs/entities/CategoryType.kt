package ru.twilightsu.walletfs.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class CategoryTypeEnum(val value: Int){
    INCOME(1),
    EXPENSE(2),
    TRANSFER(3),
    BALANCE(4)
}

class CategoryTypeEnumConverter{
    @TypeConverter
    fun fromCategoryTypeEnum(value: CategoryTypeEnum) = value.value

    @TypeConverter
    fun toCategoryTypeEnum(value: Int) = enumValues<CategoryTypeEnum>().find { it.value == value }
}

@Entity(tableName = "category_types")
data class CategoryType(
    @PrimaryKey @ColumnInfo(name = "category_type_id") val Id: Int,
    @ColumnInfo(name = "category_type_name") val Name: String
)

fun CategoryTypeEnum.toCategoryType() : CategoryType  = CategoryType(
    Id = value,
    Name = name
)

@Dao
interface CategoryTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(categoryType: CategoryType)
}
