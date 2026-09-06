package com.example.draw.camera.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.draw.camera.filter.FaceFilterProcessor
import com.example.draw.camera.model.FilterMode
import com.google.mlkit.vision.face.Face

class FaceOverlayView(
    context: Context, attrs: AttributeSet?
) : View(
    context, attrs
) {

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )

    private var faces: List<Face> = emptyList()

    private var filterMode = FilterMode.HEAD

    private var filterBitmap: Bitmap? = null

    fun updateFaces(
        faces: List<Face>
    ) {
        this.faces = faces

        invalidate()
    }

    fun updateFilter(
        mode: FilterMode, bitmap: Bitmap?
    ) {
        val old = filterBitmap

        filterMode = mode

        filterBitmap = bitmap

        if (old != null && old !== bitmap && !old.isRecycled) {
            old.recycle()
        }

        invalidate()
    }

    fun clearFaces() {
        faces = emptyList()

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(
            canvas
        )

        val bitmap = filterBitmap ?: return

        faces.forEach { face ->

            val placement = FaceFilterProcessor.getPlacement(
                    face, filterMode, bitmap
                )

            canvas.save()

            canvas.rotate(
                placement.rotation, placement.rect.centerX(), placement.rect.centerY()
            )

            canvas.drawBitmap(
                bitmap, null, placement.rect, paint
            )

            canvas.restore()
        }
    }

    override fun onDetachedFromWindow() {
        filterBitmap?.takeIf {
                !it.isRecycled
            }?.recycle()

        filterBitmap = null

        super.onDetachedFromWindow()
    }
}