package com.example.draw.camera.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.exifinterface.media.ExifInterface
import com.example.draw.camera.filter.DrawableBitmapLoader
import com.example.draw.camera.filter.FaceFilterProcessor
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FilterMode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class CameraCaptureProcessor(
    private val context: Context
) {

    private val executor = Executors.newSingleThreadExecutor()

    private val main = Handler(
        Looper.getMainLooper()
    )

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()

            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
            )

            .setLandmarkMode(
                FaceDetectorOptions.LANDMARK_MODE_ALL
            )

            .build()
    )

    fun process(
        inputUri: Uri, cameraSize: CameraSize, filterMode: FilterMode,

        @DrawableRes filterRes: Int?,

        onSuccess: (Uri) -> Unit,

        onError: (Throwable) -> Unit
    ) {
        executor.execute {
            runCatching {
                val file = File(
                    requireNotNull(
                        inputUri.path
                    )
                )

                val oriented = loadOrientedBitmap(
                    file
                )

                val bitmap = cropBitmap(
                    oriented, cameraSize
                )

                if (oriented !== bitmap && !oriented.isRecycled) {
                    oriented.recycle()
                }

                if (filterRes == null) {
                    val output = saveBitmap(
                        bitmap
                    )

                    success(
                        output, onSuccess
                    )

                    return@execute
                }

                val filter = DrawableBitmapLoader.load(
                    context = context,

                    resource = filterRes,

                    targetWidth = 1200
                ) ?: throw IllegalStateException(
                    "Không đọc được filter"
                )

                val image = InputImage.fromBitmap(
                    bitmap, 0
                )

                detector.process(
                    image
                ).addOnSuccessListener { faces ->

                    executor.execute {
                        runCatching {
                            val mutable = if (bitmap.isMutable) {
                                bitmap
                            } else {
                                bitmap.copy(
                                    Bitmap.Config.ARGB_8888, true
                                )
                            }

                            val canvas = Canvas(
                                mutable
                            )

                            val paint = Paint(
                                Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
                            )

                            faces.forEach { face ->

                                val placement = FaceFilterProcessor.getPlacement(
                                    face, filterMode, filter
                                )

                                canvas.save()

                                canvas.rotate(
                                    placement.rotation,
                                    placement.rect.centerX(),
                                    placement.rect.centerY()
                                )

                                canvas.drawBitmap(
                                    filter, null, placement.rect, paint
                                )

                                canvas.restore()
                            }

                            if (!filter.isRecycled) {
                                filter.recycle()
                            }

                            val output = saveBitmap(
                                mutable
                            )

                            success(
                                output, onSuccess
                            )

                        }.onFailure {
                            error(
                                it, onError
                            )
                        }
                    }
                }

                    .addOnFailureListener {
                        if (!filter.isRecycled) {
                            filter.recycle()
                        }

                        error(
                            it, onError
                        )
                    }

            }.onFailure {
                error(
                    it, onError
                )
            }
        }
    }

    private fun cropBitmap(
        bitmap: Bitmap, cameraSize: CameraSize
    ): Bitmap {

        if (cameraSize == CameraSize.FULL) {
            return bitmap
        }

        val targetRatio = when (cameraSize) {
            CameraSize.S1_1 -> 1f

            CameraSize.S4_3 -> 3f / 4f

            CameraSize.S16_9 -> 9f / 16f

            CameraSize.FULL -> return bitmap
        }

        val sourceRatio = bitmap.width.toFloat() / bitmap.height

        var cropWidth = bitmap.width

        var cropHeight = bitmap.height

        if (sourceRatio > targetRatio) {
            cropWidth = (bitmap.height * targetRatio).roundToInt()

        } else {
            cropHeight = (bitmap.width / targetRatio).roundToInt()
        }

        val left = (bitmap.width - cropWidth) / 2

        val top = (bitmap.height - cropHeight) / 2

        return Bitmap.createBitmap(
            bitmap, left, top, cropWidth, cropHeight
        )
    }

    private fun loadOrientedBitmap(
        file: File
    ): Bitmap {

        val bitmap = BitmapFactory.decodeFile(
            file.absolutePath
        ) ?: throw IllegalStateException(
            "Không đọc được ảnh"
        )

        val exif = ExifInterface(
            file
        )

        val rotation = exif.rotationDegrees

        val flipped = exif.isFlipped

        if (rotation == 0 && !flipped) {
            return bitmap
        }

        val matrix = Matrix()

        if (flipped) {
            matrix.postScale(
                -1f, 1f
            )
        }

        if (rotation != 0) {
            matrix.postRotate(
                rotation.toFloat()
            )
        }

        val output = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        if (output !== bitmap && !bitmap.isRecycled) {
            bitmap.recycle()
        }

        return output
    }

    private fun saveBitmap(
        bitmap: Bitmap
    ): Uri {

        val file = File(
            context.cacheDir,

            "camera_final_${
                System.currentTimeMillis()
            }.jpg"
        )

        FileOutputStream(
            file
        ).use {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 95, it
            )
        }

        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }

        return Uri.fromFile(
            file
        )
    }

    private fun success(
        uri: Uri, callback: (Uri) -> Unit
    ) {
        main.post {
            callback(
                uri
            )
        }
    }

    private fun error(
        throwable: Throwable, callback: (Throwable) -> Unit
    ) {
        main.post {
            callback(
                throwable
            )
        }
    }

    fun close() {
        detector.close()

        executor.shutdown()
    }
}