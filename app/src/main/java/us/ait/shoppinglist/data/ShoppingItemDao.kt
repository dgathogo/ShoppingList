package us.ait.shoppinglist.data

import androidx.room.*

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shoppingitem")
    fun getAllTodo() : List<ShoppingItem>

    @Insert
    fun addTodo(todo: ShoppingItem) : Long

    @Delete
    fun deleteTodo(todo: ShoppingItem)

    @Update
    fun updateTodo(todo: ShoppingItem)

    @Query("DELETE FROM shoppingitem")
    fun deleteAllTodo()

}