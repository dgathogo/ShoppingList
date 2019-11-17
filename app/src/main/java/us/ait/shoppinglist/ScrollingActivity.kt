package us.ait.shoppinglist

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
        val KEY_ITEM = "KEY_ITEM"
        val KEY_STARTED = "KEY_STARTED"
        val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
        val TAG_ITEM_DETAILS = "TAG_ITEM_DETAILS"
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
                .setPrimaryText("New item")
                .setSecondaryText("Click here to create new items")
                .show()
            saveWasStarted()
        }
    }

    private fun saveWasStarted() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.apply()
    }

    private fun wasStartedBefore(): Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
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
