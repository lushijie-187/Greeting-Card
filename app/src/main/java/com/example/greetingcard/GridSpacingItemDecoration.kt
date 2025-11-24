// file: com/example/greetingcard/GridSpacingItemDecoration.kt
package com.example.greetingcard

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 为 StaggeredGridLayoutManager 或 GridLayoutManager 添加间距的 ItemDecoration。
 *
 * @param spanCount 列数
 * @param spacing 每个 item 之间的间距
 * @param includeEdge 是否包括列表的左右边缘
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        // 获取 item 在span中的索引
        val spanIndex = (view.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.spanIndex ?: (position % spanCount)

        if (includeEdge) {
            // 如果包括边缘
            outRect.left = spacing - spanIndex * spacing / spanCount
            outRect.right = (spanIndex + 1) * spacing / spanCount
        } else {
            // 如果不包括边缘
            outRect.left = spanIndex * spacing / spanCount
            outRect.right = spacing - (spanIndex + 1) * spacing / spanCount
        }

        if (position < spanCount) { // top edge
            outRect.top = spacing
        }
        outRect.bottom = spacing // item bottom
    }
}
