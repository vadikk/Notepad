package com.example.notepad.ui.mainScreen.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.data.utils.toPx

class NoteItemDecoration: RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (itemPosition == 0) outRect.set(10.toPx, 5.toPx, 10.toPx, 5.toPx)
        else if (itemCount > 0 && itemPosition == itemCount - 1) outRect.set(10.toPx,0, 10.toPx, 5.toPx)
        else outRect.set(10.toPx, 5.toPx, 10.toPx, 5.toPx)
    }
}