package com.example.draw.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val listSticker = mutableListOf<TextSticker>()

    private var mode = 0

    init {
        for (char in 'A'..'Z') {
            val text = TextSticker(context)
            text.setText(char.toString())
            listSticker.add(text)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawText(canvas)
    }

    fun drawText(canvas: Canvas) {

        for (i in 0 until listSticker.size) {

            val textSticker = listSticker[i]

            textSticker.matrix.reset()

            var row = 0
            var column = 0

            if (i in 0..6) {
                row = 0
                column = i
            }

            if (i in 7..13) {
                row = 1
                column = i - 7
            }

            if (i in 14..19) {
                row = 2
                column = i - 14
            }

            if (i in 20..25) {
                row = 3
                column = i - 20
            }

            val x = 100f + column * 100f
            val y = 150f + row * 150f

            canvas.save()

            when (mode) {

                0 -> {
                    if (row < 2) {
                        textSticker.matrix.postTranslate(x, y)
                    } else {
                        textSticker.matrix.postRotate(180f)
                        textSticker.matrix.postTranslate(x, y)
                    }
                }

                1 -> {
                    textSticker.matrix.postTranslate(x, y)
                }

                2 -> {
                    canvas.translate(x, y)
                    textSticker.matrix.preScale(2f, 2f)
                }

                3 -> {
                    canvas.translate(x, y)
                    textSticker.matrix.postScale(2f, 2f)
                }

                4 -> {
                    canvas.translate(x, y)
                    textSticker.matrix.postRotate(45f)
                }

                5 -> {
                    textSticker.matrix.setSkew(0.5f, 0f)
                    textSticker.matrix.postTranslate(x, y)
                }

                6 -> {
                    textSticker.matrix.preRotate(0f)
                    textSticker.matrix.postTranslate(x, y)
                }

                7 -> {
                    textSticker.matrix.postRotate(30f)
                    textSticker.matrix.postTranslate(x, y)
                    textSticker.matrix.postScale(1.5f, 1.5f, 5f, 5f)
                }
            }

            canvas.concat(textSticker.matrix)

            textSticker.onDraw(canvas)

            canvas.restore()
        }
    }

    fun show() {
        mode = 0
        invalidate()
    }

    fun test1() {
        mode = 1
        invalidate()
    }

    fun test2() {
        mode = 2
        invalidate()
    }

    fun test3() {
        mode = 3
        invalidate()
    }

    fun test4() {
        mode = 4
        invalidate()
    }

    fun test5() {
        mode = 5
        invalidate()
    }

    fun test6() {
        mode = 6
        invalidate()
    }

    fun test7() {
        mode = 7
        invalidate()
    }

    fun setFont(typeface: Typeface) {
        for (textSticker in listSticker) {
            textSticker.setFont(typeface)
        }

        invalidate()
    }

    fun setColor(color: Int) {
        for (textSticker in listSticker) {
            textSticker.setColor(color)
        }
        invalidate()
    }

    fun addBitmap(bitmap: Bitmap) {
        val sticker = TextSticker(context)
        sticker.setBitmap(bitmap)
        listSticker.add(sticker)
        invalidate()
    }
}