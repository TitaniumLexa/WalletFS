package ru.twilightsu.walletfs.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "category_id") val Id: Int,
    @ColumnInfo(name="category_name") val Name : String,
    @ColumnInfo(name="category_type_id") val CategoryTypeId: Int
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE category_type_id = :categoryTypeId")
    fun getAllTyped(categoryTypeId: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE category_id = :id")
    fun get(id: Int): Flow<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)
}