package us.ait.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shoppingitem")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "name") var itemName: String,
    @ColumnInfo(name = "price") var itemPrice: Long,
    @ColumnInfo(name = "category") var itemCategory: String,
    @ColumnInfo(name = "description") var itemDescription: String,
    @ColumnInfo(name = "purchased") var purchased: Boolean
): Serializable