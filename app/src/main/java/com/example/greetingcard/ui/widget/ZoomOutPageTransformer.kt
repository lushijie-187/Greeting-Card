// file: com/example/greetingcard/ZoomOutPageTransformer.kt
package com.example.greetingcard.ui.widget

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

/**
 * 一个自定义的 PageTransformer，实现了缩放和淡入淡出的切换效果。
 */
class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    /**
     * 此方法在每次屏幕滚动时被调用。
     * @param page 正在被转换的页面 View。
     * @param position 页面相对于屏幕中心的位置。
     *                 -1f: 页面在左侧屏幕外
     *                  0f: 页面在屏幕正中心
     *                  1f: 页面在右侧屏幕外
     *                 (-1f, 1f): 页面在屏幕上部分可见
     */
    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            val pageHeight = height

            when {
                // Case 1: 页面在屏幕中心位置的左侧或右侧，但仍在 [-1, 1] 范围内
                position >= -1f && position <= 1f -> {
                    // 计算缩放比例。position 越接近0，scaleFactor 越接近1。
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))

                    // 计算页面在垂直和水平方向上的边距
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2

                    // 通过设置 translation 来补偿缩放带来的位移，使页面看起来是向中心缩放
                    if (position < 0) { // 页面在左侧
                        translationX = horzMargin - vertMargin / 2
                    } else { // 页面在右侧
                        translationX = -horzMargin + vertMargin / 2
                    }

                    // 应用缩放
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // 根据缩放比例计算并应用透明度
                    // 保证透明度在 [MIN_ALPHA, 1] 区间
                    alpha =
                        (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }

                // Case 2: 页面已经完全滑出屏幕（position < -1 或 position > 1）
                else -> {
                    // 对于屏幕外的页面，将其透明度设为0，避免在某些情况下意外可见
                    alpha = 0f
                }
            }
        }
    }
}
