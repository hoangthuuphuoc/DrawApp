package com.example.draw.camera.filter

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap

object DrawableBitmapLoader {

    fun load(
        context: Context, @DrawableRes resource: Int, targetWidth: Int =512
    ): Bitmap? {

        val drawable = AppCompatResources.getDrawable(
                context, resource
            ) ?: return null

        val originalWidth = drawable.intrinsicWidth.coerceAtLeast(1)

        val originalHeight = drawable.intrinsicHeight.coerceAtLeast(1)

        val height =
            (targetWidth.toFloat() / originalWidth * originalHeight).toInt().coerceAtLeast(1)

        return drawable.toBitmap(
            width = targetWidth,

            height = height,

            config = Bitmap.Config.ARGB_8888
        )
    }
}