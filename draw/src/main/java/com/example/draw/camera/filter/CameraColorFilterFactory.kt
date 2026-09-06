package com.example.draw.camera.filter

import android.graphics.ColorMatrix
import com.example.draw.camera.model.CameraColorFilter

object CameraColorFilterFactory {


    fun create(
        filter: CameraColorFilter
    ): ColorMatrix {

        return when (filter) {

            CameraColorFilter.ORIGINAL -> {
                ColorMatrix()
            }

            CameraColorFilter.WARM -> {
                ColorMatrix(
                    floatArrayOf(
                        1.10f,
                        0f,
                        0f,
                        0f,
                        8f,
                        0f,
                        1.03f,
                        0f,
                        0f,
                        3f,
                        0f,
                        0f,
                        0.90f,
                        0f,
                        -3f,
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                    )
                )
            }

            CameraColorFilter.COOL -> {
                ColorMatrix(
                    floatArrayOf(
                        0.92f,
                        0f,
                        0f,
                        0f,
                        -2f,
                        0f,
                        1.02f,
                        0f,
                        0f,
                        1f,
                        0f,
                        0f,
                        1.12f,
                        0f,
                        7f,
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                    )
                )
            }

            CameraColorFilter.BLACK_WHITE -> {
                ColorMatrix().apply {
                    setSaturation(
                        0f
                    )
                }
            }

            CameraColorFilter.VINTAGE -> {
                ColorMatrix(
                    floatArrayOf(
                        0.90f,
                        0.15f,
                        0.05f,
                        0f,
                        8f,
                        0.08f,
                        0.82f,
                        0.08f,
                        0f,
                        4f,
                        0.05f,
                        0.12f,
                        0.75f,
                        0f,
                        -3f,
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                    )
                )
            }

            CameraColorFilter.FADE -> {
                ColorMatrix().apply {
                    setSaturation(
                        0.72f
                    )
                }
            }
        }
    }
}