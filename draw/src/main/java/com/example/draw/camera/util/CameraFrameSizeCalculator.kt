package com.example.draw.camera.util

import com.example.draw.camera.model.CameraSize

object CameraFrameSizeCalculator {

    fun getHeight(
        width: Int,
        height: Int,
        cameraSize: CameraSize
    ): Float {
        if (width <= 0 || height <= 0) {
            return 0f
        }

        return when (cameraSize) {
            CameraSize.S1_1 -> width.toFloat()
            CameraSize.S4_3 -> width * 4f / 3f
            CameraSize.S16_9 -> width * 16f / 9f
            CameraSize.FULL -> height.toFloat()
        }.coerceAtMost(
            height.toFloat()
        )
    }
}
