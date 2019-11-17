package us.ait.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.android.synthetic.main.item_row.view.*
import us.ait.shoppinglist.R
import us.ait.shoppinglist.ScrollingActivity
import us.ait.shoppinglist.data.AppDatabase
import us.ait.shoppinglist.data.ShoppingItem
import us.ait.shoppinglist.touch.ShoppingTouchHelperCallBack
import java.util.*

class ShoppingListAdapter : Adapter<ShoppingListAdapter.ViewHolder>, ShoppingTouchHelperCallBack {

    private val shoppingList = mutableListOf<ShoppingItem>()
    private val context: Context

    constructor(context: Context, shoppingItems: List<ShoppingItem>) {
        this.context = context
        shoppingList.addAll(shoppingItems)
    }

    override fun getItemCount(): Int {
        return shoppingList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shoppingListRow = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )
        return ViewHolder(shoppingListRow)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = shoppingList[holder.adapterPosition]
        holder.cbPurchased.isChecked = item.purchased

        holder.tvPrice.text = item.itemPrice.toString()
        holder.tvName.text = item.itemName

        var icon: Int =
            when (item.itemCategory) {
                0 -> R.drawable.food
                1 -> R.drawable.clothing
                2 -> R.drawable.books
                3 -> R.drawable.electronics
                4 -> R.drawable.other
                else -> {
                    throw RuntimeException(context.getString(R.string.error_missing_category))
                }
            }

        holder.itemIcon.setImageResource(icon)

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
            (context as ScrollingActivity).showDetailsDialog(item)
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

    private fun deleteItem(index: Int) {
        Thread {
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

    private fun updateItem(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(context).shoppingItemDao().updateItem(item)
        }.start()
    }

    fun addItem(item: ShoppingItem) {
        shoppingList.add(item)
        notifyItemInserted(shoppingList.lastIndex)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbPurchased: CheckBox = itemView.cbPurchased
        val tvName: TextView = itemView.tvName
        val tvPrice: TextView = itemView.tvPrice
        val btnDelete: Button = itemView.btnDelete
        val btnEdit: Button = itemView.btnEdit
        val btnDetails: Button = itemView.btnDetails
        val itemIcon: ImageView = itemView.itemIcon
    }

}