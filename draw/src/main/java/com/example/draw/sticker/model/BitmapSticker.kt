package com.example.draw.sticker.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class BitmapSticker(
    private var bitmap: Bitmap
) : BaseSticker() {

    private val bitmapPaint = Paint(
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )

    init {
        updateBounds()
    }

    override fun drawContent(
        canvas: Canvas
    ) {

        canvas.drawBitmap(
            bitmap, -bitmap.width / 2f, -bitmap.height / 2f, bitmapPaint
        )
    }

    override fun updateBounds() {

        bounds.set(
            -bitmap.width / 2f, -bitmap.height / 2f, bitmap.width / 2f, bitmap.height / 2f
        )
    }

    fun setBitmap(
        newBitmap: Bitmap
    ) {

        bitmap = newBitmap

        updateBounds()
    }
}