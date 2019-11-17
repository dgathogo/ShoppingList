package us.ait.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import us.ait.shoppinglist.data.ShoppingItem

class ShoppingDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    interface ShoppingHandler {
        fun itemCreated(item: ShoppingItem)
        fun itemUpdated(item: ShoppingItem)
    }

    private lateinit var shoppingHandler: ShoppingHandler
    private lateinit var etItemName: EditText
    private lateinit var etItemDescription: EditText
    private lateinit var spinnerItemCategory: Spinner
    private lateinit var etItemPrice: EditText

    var isEditMode: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ShoppingHandler) {
            shoppingHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.error_interface_not_implemented)
            )
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_item_title))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )
        etItemName = rootView.etName
        etItemDescription = rootView.etDescription
        etItemName = rootView.etName
        etItemPrice = rootView.etPrice
        spinnerItemCategory = rootView.spCategory
        builder.setView(rootView)

        val adapter = ArrayAdapter.createFromResource(
            context as ScrollingActivity,
            R.array.Categories,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerItemCategory.adapter = adapter
        spinnerItemCategory.onItemSelectedListener = this

        spinnerItemCategory.adapter = adapter

        etItemDescription.setText(getString(R.string.default_description))

        isEditMode = (arguments != null) && arguments!!.containsKey(ScrollingActivity.KEY_ITEM)

        if (isEditMode) {
            builder.setTitle(getString(R.string.edit_item_title))
            var shoppingItem =
                arguments?.getSerializable(ScrollingActivity.KEY_ITEM) as ShoppingItem

            etItemName.setText(shoppingItem.itemName)
            etItemDescription.setText(shoppingItem.itemDescription)
            etItemPrice.setText(shoppingItem.itemPrice.toString())
            spinnerItemCategory.setSelection(shoppingItem.itemCategory)
        }

        builder.setPositiveButton("SAVE") { _, _ -> }
        builder.setNegativeButton("CANCEL") { _, _ -> }
        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isEmpty()) {
                etItemName.error = getString(R.string.error_empty_field)
            } else if (etItemPrice.text.isEmpty()) {
                etItemPrice.error = getString(R.string.error_empty_field)
            } else {
                if (isEditMode) {
                    handleShoppingItemEdit()
                } else {
                    handleShoppingItemCreate()
                }
                (dialog as AlertDialog).dismiss()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

    private fun handleShoppingItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM
        ) as ShoppingItem
        itemToEdit.itemName = etItemName.text.toString()
        itemToEdit.itemPrice = etItemPrice.text.toString().toLong()
        itemToEdit.itemDescription = etItemDescription.text.toString()
        itemToEdit.itemCategory = spinnerItemCategory.selectedItemPosition

        shoppingHandler.itemUpdated(itemToEdit)
    }

    private fun handleShoppingItemCreate() {
        shoppingHandler.itemCreated(
            ShoppingItem(
                null,
                etItemName.text.toString(),
                etItemPrice.text.toString().toLong(),
                spinnerItemCategory.selectedItemPosition,
                etItemDescription.text.toString(),
                false
            )
        )
    }
}