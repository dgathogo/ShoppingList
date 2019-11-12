package us.ait.shoppinglist.touch

interface ShoppingTouchHelperCallBack {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}