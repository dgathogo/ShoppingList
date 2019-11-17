package us.ait.shoppinglist

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.shopping_item_details.view.*
import us.ait.shoppinglist.data.ShoppingItem

class DetailsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Item Details")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.shopping_item_details, null
        )
        var shoppingItem =
            arguments?.getSerializable(ScrollingActivity.KEY_ITEM) as ShoppingItem
        rootView.tvName.setText(shoppingItem.itemName)
        rootView.tvPrice.setText(shoppingItem.itemPrice.toString())
        rootView.tvDescription.setText(shoppingItem.itemDescription)
        rootView.cbBought.isChecked = shoppingItem.purchased

        var icon =
            when (shoppingItem.itemCategory) {
                0 -> R.drawable.food
                1 -> R.drawable.clothing
                2 -> R.drawable.books
                3 -> R.drawable.electronics
                4 -> R.drawable.other
                else -> {
                    throw RuntimeException("The category does not exist")
                }
            }
        rootView.itemIcon.setImageResource(icon)
        builder.setView(rootView)

        builder.setNegativeButton("Dismiss") { _, _ -> }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        val negativeButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_NEGATIVE)
        negativeButton.setOnClickListener {
            (dialog as AlertDialog).dismiss()
        }

    }
}