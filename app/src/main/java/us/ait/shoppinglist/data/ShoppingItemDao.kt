package us.ait.shoppinglist.data

import androidx.room.*

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shoppingitem")
    fun getAllItems() : List<ShoppingItem>

    @Insert
    fun addItem(shoppingItem: ShoppingItem) : Long

    @Delete
    fun deleteItem(shoppingItem: ShoppingItem)

    @Update
    fun updateItem(shoppingItem: ShoppingItem)

    @Query("DELETE FROM shoppingitem")
    fun deleteAllItems()

}