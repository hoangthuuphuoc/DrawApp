package com.example.draw.sticker.model

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF

abstract class BaseSticker {

    val matrix = Matrix()

    val bounds = RectF()

    abstract fun drawContent(canvas: Canvas)

    abstract fun updateBounds()

    fun draw(canvas: Canvas) {
        canvas.save()

        canvas.concat(matrix)

        drawContent(canvas)

        canvas.restore()
    }

    fun contains(
        x: Float, y: Float
    ): Boolean {

        val inverse = Matrix()

        if (!matrix.invert(inverse)) {
            return false
        }

        val point = floatArrayOf(
            x, y
        )

        inverse.mapPoints(point)

        return bounds.contains(
            point[0], point[1]
        )
    }

    fun getCenter(): FloatArray {

        val point = floatArrayOf(0f, 0f)

        matrix.mapPoints(point)

        return point
    }
}