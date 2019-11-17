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
        rootView.tvName.text = shoppingItem.itemName
        rootView.tvPrice.text = shoppingItem.itemPrice.toString()
        rootView.tvDescription.text = shoppingItem.itemDescription
        rootView.cbBought.isChecked = shoppingItem.purchased

        var categories = resources.getStringArray(R.array.Categories)
        rootView.tvCategory.text = categories[shoppingItem.itemCategory]


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