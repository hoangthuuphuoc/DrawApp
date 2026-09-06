package com.example.draw.camera.filter

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import com.example.draw.camera.model.FilterMode
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.atan2
import kotlin.math.sqrt

object FaceFilterProcessor {

    data class Placement(
        val rect: RectF, val rotation: Float
    )

    fun getPlacement(
        face: Face, mode: FilterMode, bitmap: Bitmap
    ): Placement {

        val rect = RectF(
            face.boundingBox
        )

        return when (mode) {

            FilterMode.HEAD -> head(
                face, rect, bitmap
            )

            FilterMode.FACE -> face(
                face, rect, bitmap
            )

            FilterMode.EYES -> eyes(
                face, rect, bitmap
            )

            FilterMode.NOSE -> nose(
                face, rect, bitmap
            )

            FilterMode.MOUTH -> mouth(
                face, rect, bitmap
            )
        }
    }

    private fun head(
        face: Face, rect: RectF, bitmap: Bitmap
    ): Placement {

        val width = rect.width() * 1.35f

        val height = bitmapHeight(
            bitmap, width
        )

        val bottom = rect.top + rect.height() * 0.12f

        return Placement(
            RectF(
                rect.centerX() - width / 2f,

                bottom - height,

                rect.centerX() + width / 2f, bottom
            ), eyeAngle(face)
        )
    }

    private fun face(
        face: Face, rect: RectF, bitmap: Bitmap
    ): Placement {

        val width = rect.width() * 1.12f

        return Placement(
            centerRect(
                rect.centerX(), rect.centerY(), width, bitmapHeight(
                    bitmap, width
                )
            ), eyeAngle(face)
        )
    }

    private fun eyes(
        face: Face, rect: RectF, bitmap: Bitmap
    ): Placement {

        val left = landmark(
            face, FaceLandmark.LEFT_EYE
        )

        val right = landmark(
            face, FaceLandmark.RIGHT_EYE
        )

        if (left == null || right == null) {
            val width = rect.width() * 0.9f

            return Placement(
                centerRect(
                    rect.centerX(), rect.top + rect.height() * 0.38f, width, bitmapHeight(
                        bitmap, width
                    )
                ), 0f
            )
        }

        val eyeDistance = distance(
            left, right
        )

        val width = eyeDistance * 2.25f

        return Placement(
            centerRect(
                (left.x + right.x) / 2f,

                (left.y + right.y) / 2f,

                width,

                bitmapHeight(
                    bitmap, width
                )
            ),

            angle(
                left, right
            )
        )
    }

    private fun nose(
        face: Face, rect: RectF, bitmap: Bitmap
    ): Placement {

        val point = landmark(
            face, FaceLandmark.NOSE_BASE
        )

        val width = rect.width() * 0.36f

        return Placement(
            centerRect(
                point?.x ?: rect.centerX(),

                point?.y ?: (rect.top + rect.height() * 0.58f),

                width,

                bitmapHeight(
                    bitmap, width
                )
            ),

            eyeAngle(face)
        )
    }

    private fun mouth(
        face: Face, rect: RectF, bitmap: Bitmap
    ): Placement {

        val left = landmark(
            face, FaceLandmark.MOUTH_LEFT
        )

        val right = landmark(
            face, FaceLandmark.MOUTH_RIGHT
        )

        val bottom = landmark(
            face, FaceLandmark.MOUTH_BOTTOM
        )

        if (left != null && right != null) {
            val width = distance(
                left, right
            ) * 1.65f

            return Placement(
                centerRect(
                    (left.x + right.x) / 2f,

                    bottom?.y ?: (left.y + right.y) / 2f, width, bitmapHeight(
                        bitmap, width
                    )
                ), angle(
                    left, right
                )
            )
        }

        val width = rect.width() * 0.55f

        return Placement(
            centerRect(
                rect.centerX(),

                rect.top + rect.height() * 0.75f,

                width,

                bitmapHeight(
                    bitmap, width
                )
            ), eyeAngle(face)
        )
    }

    private fun landmark(
        face: Face, type: Int
    ): PointF? {

        return face.getLandmark(type)?.position
    }

    private fun eyeAngle(
        face: Face
    ): Float {

        val left = landmark(
            face, FaceLandmark.LEFT_EYE
        )

        val right = landmark(
            face, FaceLandmark.RIGHT_EYE
        )

        if (left == null || right == null) {
            return 0f
        }

        return angle(
            left, right
        )
    }

    private fun angle(
        first: PointF, second: PointF
    ): Float {

        var value = Math.toDegrees(
            atan2(
                second.y - first.y,

                second.x - first.x
            ).toDouble()
        ).toFloat()

        if (value > 90f) {
            value -= 180f
        }

        if (value < -90f) {
            value += 180f
        }

        return value
    }

    private fun distance(
        first: PointF, second: PointF
    ): Float {

        val dx = second.x - first.x

        val dy = second.y - first.y

        return sqrt(
            dx * dx + dy * dy
        )
    }

    private fun bitmapHeight(
        bitmap: Bitmap, width: Float
    ): Float {

        return width * bitmap.height.toFloat() / bitmap.width.coerceAtLeast(1).toFloat()
    }

    private fun centerRect(
        x: Float, y: Float, width: Float, height: Float
    ): RectF {

        return RectF(
            x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f
        )
    }
}