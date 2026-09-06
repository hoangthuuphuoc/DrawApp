package com.example.draw.sticker.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.scale
import com.example.draw.R
import com.example.draw.sticker.model.BaseSticker
import com.example.draw.sticker.model.BitmapSticker
import com.example.draw.sticker.model.TextSticker
import kotlin.math.atan2
import kotlin.math.sqrt

class StickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(
    context, attrs, defStyleAttr
) {

    private val stickerList = mutableListOf<BaseSticker>()
    private var touchEnabled = true
    private var selectedSticker: BaseSticker? = null
    private var selectedColor = Color.BLACK

    private var lastX = 0f
    private var lastY = 0f

    private var lastDistance = 0f
    private var lastAngle = 0f

    private var touchMode = TouchMode.NONE

    private val bitmapPaint = Paint(
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )

    private val bitmapDelete by lazy {
        BitmapFactory.decodeResource(
            resources, R.drawable.img_18
        ).scale(
            ICON_SIZE, ICON_SIZE
        )
    }

    private val bitmapRotate by lazy {
        BitmapFactory.decodeResource(
            resources, R.drawable.img_17
        ).scale(
            ICON_SIZE, ICON_SIZE
        )
    }

    private val bitmapScale by lazy {
        BitmapFactory.decodeResource(
            resources, R.drawable.img_16
        ).scale(
            ICON_SIZE, ICON_SIZE
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        for (sticker in stickerList) {
            sticker.draw(canvas)
        }

        drawBorder(canvas)
    }

    fun drawBorder(
        canvas: Canvas
    ) {

        val sticker = selectedSticker ?: return

        canvas.save()

        canvas.concat(
            sticker.matrix
        )

        canvas.drawRect(
            sticker.bounds, borderPaint
        )

        canvas.drawBitmap(
            bitmapDelete,
            sticker.bounds.left - bitmapDelete.width / 2f,
            sticker.bounds.top - bitmapDelete.height / 2f,
            bitmapPaint
        )

        canvas.drawBitmap(
            bitmapRotate,
            sticker.bounds.left - bitmapRotate.width / 2f,
            sticker.bounds.bottom - bitmapRotate.height / 2f,
            bitmapPaint
        )

        canvas.drawBitmap(
            bitmapScale,
            sticker.bounds.right - bitmapScale.width / 2f,
            sticker.bounds.bottom - bitmapScale.height / 2f,
            bitmapPaint
        )

        canvas.restore()
    }

    fun addTextSticker(
        text: String, color: Int
    ) {
        selectedColor = color
        val sticker = TextSticker(text)

        sticker.setColor(selectedColor)

        sticker.matrix.postTranslate(
            width / 2f, height / 2f
        )

        stickerList.add(
            sticker
        )

        selectedSticker = sticker

        invalidate()
    }

    fun addBitmapSticker(
        bitmap: Bitmap
    ) {

        val sticker = BitmapSticker(bitmap)
        sticker.matrix.postTranslate(
            width / 2f, height / 2f
        )
        stickerList.add(sticker)
        selectedSticker = sticker
        invalidate()
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (!touchEnabled) {
            return false
        }
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                val sticker = selectedSticker
                if (sticker != null) {
                    val point = getLocalPoint(sticker, event.x, event.y)
                    if (isIconTouched(
                            point[0], point[1], sticker.bounds.left, sticker.bounds.top
                        )
                    ) {
                        stickerList.remove(
                            sticker
                        )
                        selectedSticker = null

                        invalidate()

                        return true
                    }

                    if (isIconTouched(
                            point[0], point[1], sticker.bounds.left, sticker.bounds.bottom
                        )
                    ) {

                        touchMode = TouchMode.ROTATE

                        lastAngle = getAngle(sticker, event.x, event.y)

                        return true
                    }

                    if (isIconTouched(
                            point[0], point[1], sticker.bounds.right, sticker.bounds.bottom
                        )
                    ) {

                        touchMode = TouchMode.SCALE
                        lastDistance = getDistance(
                            sticker, event.x, event.y
                        )
                        return true
                    }
                }

                val touchedSticker = findSticker(event.x, event.y)
                selectedSticker = touchedSticker
                if (touchedSticker != null) {
                    touchMode = TouchMode.MOVE
                    lastX = event.x
                    lastY = event.y

                } else {
                    touchMode = TouchMode.NONE
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val sticker = selectedSticker ?: return true

                when (touchMode) {

                    TouchMode.MOVE -> {
                        val dx = event.x - lastX
                        val dy = event.y - lastY
                        sticker.matrix.postTranslate(dx, dy)
                        lastX = event.x
                        lastY = event.y
                    }

                    TouchMode.SCALE -> {
                        val distance = getDistance(sticker, event.x, event.y)
                        if (lastDistance > 0f && distance > 0f) {
                            val scale = (distance / lastDistance)
                            sticker.matrix.preScale(scale, scale)
                        }
                        lastDistance = distance
                    }

                    TouchMode.ROTATE -> {

                        val angle = getAngle(sticker, event.x, event.y)

                        var delta = angle - lastAngle
                        if (delta > 180f) {
                            delta -= 360f
                        }
                        if (delta < -180f) {
                            delta += 360f
                        }
                        sticker.matrix.preRotate(
                            delta
                        )
                        lastAngle = angle
                    }

                    TouchMode.NONE -> {
                    }
                }
                invalidate()

            }

            MotionEvent.ACTION_UP -> {
                touchMode = TouchMode.NONE
                performClick()
            }
        }

        return true
    }

    private fun findSticker(
        x: Float, y: Float
    ): BaseSticker? {

        for (index in stickerList.indices.reversed()) {

            val sticker = stickerList[index]

            if (sticker.contains(
                    x, y
                )
            ) {
                return sticker
            }
        }

        return null
    }

    private fun getLocalPoint(
        sticker: BaseSticker, x: Float, y: Float
    ): FloatArray {

        val inverse = Matrix()

        sticker.matrix.invert(
            inverse
        )

        val point = floatArrayOf(x, y)

        inverse.mapPoints(
            point
        )
        return point
    }

    private fun isIconTouched(
        x: Float, y: Float, iconX: Float, iconY: Float
    ): Boolean {

        val size = ICON_SIZE.toFloat()
        return x >= iconX - size && x <= iconX + size && y >= iconY - size && y <= iconY + size
    }

    private fun getDistance(
        sticker: BaseSticker, x: Float, y: Float
    ): Float {
        val center = sticker.getCenter()
        val dx = x - center[0]
        val dy = y - center[1]
        return sqrt(dx * dx + dy * dy)
    }

    private fun getAngle(
        sticker: BaseSticker, x: Float, y: Float
    ): Float {
        val center = sticker.getCenter()
        return Math.toDegrees(atan2((y - center[1]).toDouble(), (x - center[0]).toDouble()))
            .toFloat()
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private enum class TouchMode {
        NONE, MOVE, ROTATE, SCALE
    }

    companion object {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = Color.BLACK
            pathEffect= DashPathEffect(floatArrayOf(10f,10f),0f)
        }
        const val ICON_SIZE = 60
    }

    fun setTextColor(color: Int) {
        val sticker = selectedSticker
        if (sticker is TextSticker) {
            sticker.setColor(color)
            invalidate()
        }
    }

    fun setTextSize(
        size: Float
    ) {
        val sticker =
            selectedSticker

        if (sticker is TextSticker) {
           sticker.setTextSize(size)

            invalidate()
        }
    }
    fun setTextStrokeColor(color: Int) {

        val sticker = selectedSticker

        if (sticker is TextSticker) {
            sticker.setStrokeColor(color)
            invalidate()
        }
    }

    fun setTextStrokeWidth(width: Float) {

        val sticker = selectedSticker

        if (sticker is TextSticker) {
            sticker.setStrokeWidth(width)
            invalidate()
        }
    }
    fun setTouchEnabled(
        enabled: Boolean
    ) {
        touchEnabled = enabled
    }
    fun setTextFont(
        typeface: Typeface
    ) {
        val sticker = selectedSticker

        if (sticker is TextSticker) {
            sticker.setFont(typeface)
            invalidate()
        }
    }
}
