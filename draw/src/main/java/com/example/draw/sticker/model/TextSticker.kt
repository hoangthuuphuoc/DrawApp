package com.example.draw.sticker.model

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

class TextSticker(
    private var text: String
) : BaseSticker() {

    private val padding = 25f

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 80f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 80f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    init {
        updateBounds()
    }

    override fun drawContent(
        canvas: Canvas
    ) {

        val fontMetrics = textPaint.fontMetrics

        val baseline = -(fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(text, 0f, baseline, strokePaint)
        canvas.drawText(text, 0f, baseline, textPaint)
    }

    override fun updateBounds() {

        val fontMetrics = textPaint.fontMetrics

        val baseline = -(fontMetrics.ascent + fontMetrics.descent) / 2f

        val textWidth = textPaint.measureText(text)

        bounds.set(
            -textWidth / 2f - padding,
            baseline + fontMetrics.ascent - padding,
            textWidth / 2f + padding,
            baseline + fontMetrics.descent + padding
        )
    }

    fun setText(
        newText: String
    ) {

        text = newText

        updateBounds()
    }

    fun setColor(
        color: Int
    ) {
        textPaint.color = color
    }

    fun setFont(
        typeface: Typeface
    ) {

        textPaint.typeface = typeface
        strokePaint.typeface = typeface

        updateBounds()
    }

    fun setTextSize(
        textSize: Float
    ) {

        textPaint.textSize = textSize
        strokePaint.textSize = textSize

        updateBounds()
    }
    fun setStrokeColor(
        color: Int
    ) {
        strokePaint.color = color
    }
    fun setStrokeWidth(
        width: Float
    ) {
        strokePaint.strokeWidth = width
    }

}