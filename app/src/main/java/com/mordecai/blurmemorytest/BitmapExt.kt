package com.mordecai.blurmemorytest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.DrawableRes

fun Matrix?.getBitmap(@DrawableRes drawableId: Int, sx: Float? = null, sy: Float? = null, x: Int = 0, y: Int = 0): Bitmap {
   return newBitmap(BitmapFactory.decodeResource(app.resources, drawableId,  BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_4444 }), sx, sy, x, y)
}
fun Matrix?.getBitmap(bitmap: Bitmap, sx: Float? = null, sy: Float? = null, x: Int = 0, y: Int = 0): Bitmap {
    return newBitmap(bitmap, sx, sy, x, y)
}
private fun Matrix?.newBitmap(bitmap: Bitmap, sx: Float? = null, sy: Float? = null, x: Int = 0, y: Int = 0): Bitmap {
    val srcWidth = bitmap.width
    val srcHeight = bitmap.height
    if (null != this && null != sx && null != sy) {
        reset()
        setScale( sx / srcWidth, sy / srcHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, this, true)
    } else {
        return Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight)
    }
}
