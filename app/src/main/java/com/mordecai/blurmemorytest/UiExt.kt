@file:Suppress("unused")

package com.mordecai.blurmemorytest

import android.graphics.Paint

inline val Float.px2dpf: Float get(): Float = this / app.resources.displayMetrics.density
inline val Float.dp2pxf get(): Float = this * app.resources.displayMetrics.density

inline val Int.px2dp: Int get() = (this / app.resources.displayMetrics.density + 0.5f).toInt()
inline val Int.dp2px: Int get() = (this * app.resources.displayMetrics.density + 0.5f).toInt()
inline val Int.sp2px: Int get() = dp2px

/**
 * ⦁ 计算 baseline 的相对文字中心的偏移量
 *
 * ⦁ 2024-12-24
 * @author crow
 */
fun calculateBaselineOffsetY(fontMetrics: Paint.FontMetrics): Float {
    return -fontMetrics.ascent / 2f - fontMetrics.descent / 2f
}