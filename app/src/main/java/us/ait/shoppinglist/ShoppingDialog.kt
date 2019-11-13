package us.ait.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.new_item_dialog.*
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import us.ait.shoppinglist.data.ShoppingItem
import java.lang.RuntimeException
import java.util.*

class ShoppingDialog : DialogFragment() {

    interface ShoppingHandler {
        fun itemCreated(item: ShoppingItem)
        fun itemUpdated(item: ShoppingItem)
    }

    private lateinit var shoppingHandler : ShoppingHandler
    private lateinit var etItemName: EditText
    private lateinit var etItemDescription: EditText
    private lateinit var etItemCategory: EditText
    private lateinit var etItemPrice: EditText

    var isEditMode : Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ShoppingHandler) {
            shoppingHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the ShoppingHandlerInterface"
            )
        }

    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//        if (context is ShoppingHandler) {
//            shoppingHandler = context
//        } else {
//            throw RuntimeException(
//                "The activity does not implement the ShoppingHandlerInterface")
//        }
//    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New Shopping Item")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )
        etItemName = rootView.etName
        etItemDescription = rootView.etDescription
        builder.setView(rootView)

        isEditMode = (arguments != null) && arguments!!.containsKey(ScrollingActivity.KEY_ITEM)

        if (isEditMode) {
            builder.setTitle("Edit Shopping Item")
            var shoppingItem = arguments?.getSerializable(ScrollingActivity.KEY_ITEM) as ShoppingItem

            etItemName.setText(shoppingItem.itemName)
            etItemDescription.setText(shoppingItem.itemDescription)
            etItemCategory.setText(shoppingItem.itemCategory)
            etItemPrice.setText(shoppingItem.itemPrice.toString())
        }

        builder.setPositiveButton("SAVE") {
                dialog, witch -> // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isNotEmpty()) {
                if (isEditMode){
                    handleShoppingItemEdit()
                } else {
                    handleShoppingItemCreate()
                }

                (dialog as AlertDialog).dismiss()
            } else {
                etItemName.error = "This field can not be empty"
                TODO("handle multiple required fields")
            }
        }
    }

    private fun handleShoppingItemCreate() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM
        ) as ShoppingItem
        itemToEdit.itemName = etItemName.text.toString()
        itemToEdit.itemPrice = etPrice.text.toString().toLong()
        itemToEdit.itemCategory = etItemCategory.text.toString()
        itemToEdit.itemDescription = etItemName.text.toString()

        shoppingHandler.itemUpdated(itemToEdit)
    }
    private fun handleShoppingItemEdit() {
        shoppingHandler.itemCreated(
            ShoppingItem(
                null,
                etItemName.text.toString(),
                etPrice.text.toString().toLong(),
                etItemCategory.text.toString(),
                etItemDescription.text.toString(),
                false
            )
        )
    }
}