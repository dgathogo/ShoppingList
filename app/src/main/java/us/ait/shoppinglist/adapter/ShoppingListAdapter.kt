package us.ait.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import kotlinx.android.synthetic.main.item_row.view.*
import us.ait.shoppinglist.R
import us.ait.shoppinglist.ScrollingActivity
import us.ait.shoppinglist.data.AppDatabase
import us.ait.shoppinglist.data.ShoppingItem
import us.ait.shoppinglist.touch.ShoppingTouchHelperCallBack
import java.util.*

class ShoppingListAdapter : Adapter<ShoppingListAdapter.ViewHolder>, ShoppingTouchHelperCallBack {

    private val shoppingList = mutableListOf<ShoppingItem>()
    private val context : Context
    constructor(context: Context, shoppingItems: List<ShoppingItem> ) {
        this.context = context
        shoppingList.addAll(shoppingItems)
    }
    override fun getItemCount(): Int {
        return shoppingList.size
    }

    override fun onCreateViewHolder( parent: ViewGroup,viewType: Int): ViewHolder {
        val shoppingListRow = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )
        return ViewHolder(shoppingListRow)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = shoppingList.get(holder.adapterPosition)
        holder.cbPurchased.text = item.itemName
        holder.cbPurchased.isChecked = item.purchased

        holder.tvCategory.text = item.itemCategory
        holder.tvPrice.text = item.itemPrice.toString()
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.cbPurchased.setOnClickListener {
            item.purchased = holder.cbPurchased.isChecked
            updateItem(item)
        }
        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(
                item, holder.adapterPosition
            )
        }
        holder.btnDetails.setOnClickListener {
            TODO("show dialog with item details")
        }
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun deleteAll() {
        Thread {
            AppDatabase.getInstance(context).shoppingItemDao().deleteAllItems()
            (context as ScrollingActivity).runOnUiThread {
                shoppingList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    fun deleteItem(index: Int) {
        Thread{
            AppDatabase.getInstance(context).shoppingItemDao().deleteItem(shoppingList[index])
            (context as ScrollingActivity).runOnUiThread {
                shoppingList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }

    fun updateItemOnPosition(item: ShoppingItem, index: Int) {
        shoppingList.set(index, item)
        notifyItemChanged(index)
    }

    fun updateItem(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(context).shoppingItemDao().updateItem(item)
        }.start()
    }

    fun addItem(item: ShoppingItem) {
        shoppingList.add(item)
        notifyItemInserted(shoppingList.lastIndex)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbPurchased = itemView.cbPurchased
        val tvCategory = itemView.tvName
        val tvPrice = itemView.tvPrice
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val btnDetails = itemView.btnDetails
//        val itemIcon = itemView.itemIcon
    }

}