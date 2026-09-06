package com.example.draw.camera.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.util.CameraFrameSizeCalculator

class FrameOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 1.5f * resources.displayMetrics.density
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(
            120,
            255,
            255,
            255
        )
        strokeWidth = resources.displayMetrics.density
    }

    private val countdownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 72f * resources.displayMetrics.scaledDensity
        isFakeBoldText = true
    }

    private var cameraSize = CameraSize.S4_3

    private var gridEnabled = true

    private var countdown: Int? = null

    private var frameTop = 0f

    private var frameHeight = 0f

    private var animator: ValueAnimator? = null

    private var frameChangedListener: ((RectF) -> Unit)? = null

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(
            w,
            h,
            oldw,
            oldh
        )

        updateFrame(
            animated = false
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        drawFrameLines(
            canvas
        )

        if (gridEnabled) {
            drawGrid(
                canvas
            )
        }

        countdown?.let { value ->
            drawCountdown(
                canvas,
                value
            )
        }
    }

    fun setCameraSize(
        size: CameraSize
    ) {
        if (cameraSize == size) {
            return
        }

        cameraSize = size

        updateFrame(
            animated = true
        )
    }

    fun setGrid(
        enabled: Boolean
    ) {
        if (gridEnabled == enabled) {
            return
        }

        gridEnabled = enabled

        invalidate()
    }

    fun setCountdown(
        value: Int?
    ) {
        countdown = value

        invalidate()
    }

    fun setOnFrameChangedListener(
        listener: (RectF) -> Unit
    ) {
        frameChangedListener = listener

        notifyFrameChanged()
    }

    fun getFrameRect(): RectF {
        return RectF(
            0f,
            frameTop,
            width.toFloat(),
            frameTop + frameHeight
        )
    }

    private fun updateFrame(
        animated: Boolean
    ) {
        if (width <= 0 || height <= 0) {
            return
        }

        val targetHeight = CameraFrameSizeCalculator.getHeight(
            width,
            height,
            cameraSize
        ).coerceIn(
            0f,
            height.toFloat()
        )

        val targetTop = (
                height - targetHeight
                ) / 2f

        if (!animated || frameHeight <= 0f) {
            animator?.cancel()

            frameTop = targetTop
            frameHeight = targetHeight

            notifyFrameChanged()

            invalidate()

            return
        }

        val startTop = frameTop
        val startHeight = frameHeight

        animator?.cancel()

        animator = ValueAnimator.ofFloat(
            0f,
            1f
        ).apply {
            duration = 280L
            interpolator = DecelerateInterpolator()

            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float

                frameTop = startTop +
                        (targetTop - startTop) * progress

                frameHeight = startHeight +
                        (targetHeight - startHeight) * progress

                notifyFrameChanged()

                invalidate()
            }

            start()
        }
    }

    private fun drawFrameLines(
        canvas: Canvas
    ) {
        if (frameHeight <= 0f) {
            return
        }

        val bottom = frameTop + frameHeight
        val threshold = framePaint.strokeWidth

        if (frameTop > threshold) {
            canvas.drawLine(
                0f,
                frameTop,
                width.toFloat(),
                frameTop,
                framePaint
            )
        }

        if (bottom < height.toFloat() - threshold) {
            canvas.drawLine(
                0f,
                bottom,
                width.toFloat(),
                bottom,
                framePaint
            )
        }
    }

    private fun drawGrid(
        canvas: Canvas
    ) {
        if (frameHeight <= 0f) {
            return
        }

        val top = frameTop
        val bottom = frameTop + frameHeight

        val x1 = width / 3f
        val x2 = width * 2f / 3f

        val y1 = top + frameHeight / 3f
        val y2 = top + frameHeight * 2f / 3f

        canvas.drawLine(
            x1,
            top,
            x1,
            bottom,
            gridPaint
        )

        canvas.drawLine(
            x2,
            top,
            x2,
            bottom,
            gridPaint
        )

        canvas.drawLine(
            0f,
            y1,
            width.toFloat(),
            y1,
            gridPaint
        )

        canvas.drawLine(
            0f,
            y2,
            width.toFloat(),
            y2,
            gridPaint
        )
    }

    private fun drawCountdown(
        canvas: Canvas,
        value: Int
    ) {
        val centerX = width / 2f
        val centerY = frameTop + frameHeight / 2f

        val metrics = countdownPaint.fontMetrics

        val textY = centerY -
                (metrics.ascent + metrics.descent) / 2f

        canvas.drawText(
            value.toString(),
            centerX,
            textY,
            countdownPaint
        )
    }

    private fun notifyFrameChanged() {
        if (width <= 0 || frameHeight <= 0f) {
            return
        }

        frameChangedListener?.invoke(
            getFrameRect()
        )
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null

        super.onDetachedFromWindow()
    }
}
