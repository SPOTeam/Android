package com.example.spoteam_android.ui.mypage

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RightSpaceItemDecoration(
    private val rightSpacePx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // 마지막 아이템이 아니라면 오른쪽 여백을 줌
        if (position != itemCount - 1) {
            outRect.right = rightSpacePx
        }
    }
}
