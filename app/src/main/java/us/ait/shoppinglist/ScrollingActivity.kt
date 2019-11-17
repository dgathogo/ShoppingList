package us.ait.shoppinglist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import us.ait.shoppinglist.adapter.ShoppingListAdapter
import us.ait.shoppinglist.data.AppDatabase
import us.ait.shoppinglist.data.ShoppingItem
import us.ait.shoppinglist.touch.ShoppingRecyclerTouchCallBack

class ScrollingActivity : AppCompatActivity(), ShoppingDialog.ShoppingHandler {

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
        const val TAG_ITEM_DETAILS = "TAG_ITEM_DETAILS"
        const val MY_PREFS = "MY PREFERENCES"
    }

    lateinit var shoppingListAdapter: ShoppingListAdapter

    var editIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)

        initRecyclerView()


        fab.setOnClickListener {
            showAddShoppingItemDialog()
        }
        fabDeleteAll.setOnClickListener {
            shoppingListAdapter.deleteAll()

        }
        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(getString(R.string.new_item))
                .setSecondaryText(getString(R.string.new_item_helper))
                .show()
            saveWasStarted()
        }
    }

    private fun saveWasStarted() {
        var sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    private fun wasStartedBefore(): Boolean {
        var sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    private fun initRecyclerView() {
        Thread {
            var shoppingList =
                AppDatabase.getInstance(this@ScrollingActivity).shoppingItemDao().getAllItems()

            runOnUiThread {
                shoppingListAdapter = ShoppingListAdapter(this, shoppingList)
                recyclerShoppingItem.adapter = shoppingListAdapter

                var itemDecoration = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerShoppingItem.addItemDecoration(itemDecoration)

                val callback = ShoppingRecyclerTouchCallBack(shoppingListAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerShoppingItem)
            }
        }.start()
    }

    private fun showAddShoppingItemDialog() {
        ShoppingDialog().show(supportFragmentManager, TAG_ITEM_DIALOG)
    }

    fun showEditItemDialog(itemEdit: ShoppingItem, idx: Int) {
        editIndex = idx
        val editDialog = ShoppingDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, TAG_ITEM_EDIT)
    }

    fun showDetailsDialog (item: ShoppingItem) {
        val detailsDialog = DetailsDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, item)
        detailsDialog.arguments = bundle

        detailsDialog.show(supportFragmentManager, TAG_ITEM_DETAILS)
    }

    private fun saveTodo(shoppingItem: ShoppingItem) {
        Thread {
            var newId = AppDatabase.getInstance(this).shoppingItemDao().addItem(shoppingItem)
            shoppingItem.itemId = newId
            runOnUiThread {
                shoppingListAdapter.addItem(shoppingItem)
            }
        }.start()
    }

    override fun itemCreated(item: ShoppingItem) {
        saveTodo(item)
    }

    override fun itemUpdated(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(this).shoppingItemDao().updateItem(item)
            runOnUiThread {
                shoppingListAdapter.updateItemOnPosition(item, editIndex)
            }
        }.start()
    }
}
