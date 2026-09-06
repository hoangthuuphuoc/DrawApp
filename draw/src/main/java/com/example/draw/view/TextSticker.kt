package com.example.draw.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface

class TextSticker(
    private val context: Context
) {

    private var text: String? = null
    private var bitmap: Bitmap? = null

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    val matrix = Matrix()

    fun setText(text1: String) {
        text = text1
        bitmap = null
    }

    fun setBitmap(bitmap1: Bitmap) {
        bitmap = bitmap1
        text = null
    }

    fun setColor(color: Int) {
        textPaint.color = color
    }

    fun setFont(typeface: Typeface) {
        textPaint.typeface = typeface
    }

    fun onDraw(canvas: Canvas) {
        text?.let {
            canvas.drawText(
                it,
                0f,
                10f,
                textPaint
            )
        }

        bitmap?.let {
            canvas.drawBitmap(
                it,
                100f,
                500f,
                textPaint
            )
        }
    }
}