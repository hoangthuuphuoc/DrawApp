package com.example.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.draw.data.Paint1

class MoveAndLine @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var touchEnabled = true
    private val listPaint = mutableListOf<Paint1>()
    private val redo = mutableListOf<Paint1>()

    private val currentPath = Path()
    private var currentPaint = Paint()
    private var tool = 2

    private val pencilPaint = Paint().apply {
        color = context.getColor(R.color.cam)
        isAntiAlias = true
        strokeWidth = 6f
        alpha = 180
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val penPaint = Paint().apply {
        color = context.getColor(R.color.cam)
        isAntiAlias = true
        strokeWidth = 20f
        alpha = 255
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val markerPaint = Paint().apply {
        color = context.getColor(R.color.cam)
        isAntiAlias = true
        strokeWidth = 45f
        alpha = 100
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.SQUARE
    }

    private val eraserPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 50f
        alpha = 255
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val brushPaint = Paint().apply {
        color = context.getColor(R.color.cam)
        isAntiAlias = true
        strokeWidth = 30f
        alpha = 255
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        currentPaint = Paint(penPaint)
    }

    fun setTool(value: Int) {
        tool = value
    }

    fun setColor(color: Int) {
        pencilPaint.color = color
        penPaint.color = color
        markerPaint.color = color
        brushPaint.color = color
    }

    private fun getPaint(): Paint {
        return when (tool) {
            1 -> Paint(pencilPaint)
            2 -> Paint(penPaint)
            3 -> Paint(markerPaint)
            4 -> Paint(eraserPaint)
            5 -> Paint(brushPaint)
            else -> Paint(penPaint)
        }
    }

    fun undo() {
        if (listPaint.isNotEmpty()) {
            val netVe = listPaint.removeAt(listPaint.lastIndex)
            redo.add(netVe)
            invalidate()
        }
    }

    fun redo() {
        if (redo.isNotEmpty()) {
            val netVe = redo.removeAt(redo.lastIndex)
            listPaint.add(netVe)
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (item in listPaint) {
            canvas.drawPath(item.path, item.paint)
        }

        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPaint = getPaint()
                currentPath.moveTo(x, y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                listPaint.add(
                    Paint1(
                        Path(currentPath),
                        Paint(currentPaint)
                    )
                )
                redo.clear()
                currentPath.reset()
                invalidate()
            }
        }
        return true
    }
    fun setTouchEnabled(
        enabled: Boolean
    ) {
        touchEnabled = enabled
    }
}