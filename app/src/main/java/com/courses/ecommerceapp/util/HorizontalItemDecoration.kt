package com.courses.ecommerceapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//used in addToCart Recyclerview list to space the bottom of the content
class HorizontalItemDecoration(private val amount:Int = 15): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = amount
    }
}